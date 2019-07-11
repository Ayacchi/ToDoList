package model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JOptionPane;

import org.h2.jdbcx.JdbcConnectionPool;

import view.MainFrame;

public class Database {

	private Connection connection;
	public volatile static JdbcConnectionPool connectionPool;
	private Lock lock = new ReentrantLock();

	protected Database() {
		if (connectionPool == null) {
			connect();
		}
		if (connectionPool.getMaxConnections() != 20) {
			connectionPool.setMaxConnections(20);
			connectionPool.setLoginTimeout(2);
		}

		try {
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Cannot connect to the database.", "Error 001", JOptionPane.ERROR_MESSAGE);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	protected void connect() {

		try {
			if (connectionPool == null) {
				connectionPool = JdbcConnectionPool.create("jdbc:h2:~/TODOList", "TODOList", "Secret");
			}

			if (connectionPool.getActiveConnections() > 0) {
				return; // this way a connection is used before it's definitely closed
			}

			connection = connectionPool.getConnection();

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Cannot connect to the database.", "Error 002", JOptionPane.ERROR_MESSAGE);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	protected void createTables() {
		lock.lock();

		String sql = "CREATE TABLE tasks(id INT AUTO_INCREMENT, task_name VARCHAR(100) NOT NULL, task_description VARCHAR(1000)); "
				+ "CREATE TABLE task_dates (id INT AUTO_INCREMENT, add_date DATE NOT NULL, finish_date DATE, deadline DATE, deadline_time TIME); "
				+ "ALTER TABLE task_dates ADD UNIQUE (id); " + "ALTER TABLE tasks ADD PRIMARY KEY (id); "
				+ "ALTER TABLE task_dates ADD FOREIGN KEY (id) REFERENCES tasks(id); ";

		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.execute();

			statement.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Cannot connect properly to the database.", "Error 003", JOptionPane.ERROR_MESSAGE);
		} finally {
			lock.unlock();
		}
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected int getAddedTasksCount() {
		lock.lock();

		String sql = "SELECT COUNT(*) FROM tasks";
		int count;

		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();

			count = resultSet.getInt(1);
			statement.close();

			return count;
		} catch (SQLException e) {
			// it is fine, since it SHOULD not exist at the beginning
			return 0;
			
		} finally {
			lock.unlock();
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected boolean testDBTables() {

		if (getAddedTasksCount() == 0) {
			return false;
		}

		return true;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected void addNewTask(Task task) {
		lock.lock();

		String sql = "INSERT INTO tasks (task_name, task_description) VALUES (?, ?)";
		String setAddDateSql;

		if (task.getTaskStatus().equals(IsDone.no)) {
			if (task.getDeadlineStatus().equals(Deadline.no)) {
				setAddDateSql = "INSERT INTO task_dates (add_date) values (?)";
			} else {
				setAddDateSql = "INSERT INTO task_dates (add_date, deadline, deadline_time) values (?, ?, ?)";
			}
		} else {
			setAddDateSql = "INSERT INTO task_dates (add_date, finish_date) values (?, ?)";
		}

		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, task.getTaskName());
			statement.setString(2, task.getTaskDescription());
			statement.execute();

			statement.close();

			PreparedStatement statement2 = connection.prepareStatement(setAddDateSql);

			statement2.setDate(1, Date.valueOf(task.getAddDate()));

			if (task.getTaskStatus().equals(IsDone.no)) {
				if (task.getDeadlineStatus().equals(Deadline.yes)) {
					statement2.setDate(2, Date.valueOf(task.getDeadline()));
					
					if (task.getDeadlineTime().length() == 5) {
						// needed for import - because in Task class deadline time data is stored without seconds (because seconds are redundant)
						statement2.setTime(3, Time.valueOf(task.getDeadlineTime() + ":00"));
					} else {
						statement2.setTime(3, Time.valueOf(task.getDeadlineTime()));
					}
				}
			} else {
				statement2.setDate(2, Date.valueOf(task.getFinishDate()));
			}

			statement2.execute();

			statement2.close();

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Cannot add new task to the database.", "Error 004", JOptionPane.ERROR_MESSAGE);
		} finally {
			lock.unlock();
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected void deleteEmptyTables() {

		if (getAddedTasksCount() == 0) {
			String sql = "DROP TABLE task_dates; DROP TABLE tasks";

			try {
				PreparedStatement statement = connection.prepareStatement(sql);
				statement.execute();

				statement.close();
			} catch (SQLException e) {
				// at first there aren't any tables to drop, so it's fine
			}
		}

	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected ArrayList<Task> getAllTasks() {
		lock.lock();

		String sql = "SELECT tasks.id, task_name, task_description, add_date, finish_date, deadline, deadline_time "
				+ "FROM tasks " + "JOIN task_dates " + "ON tasks.id = task_dates.id " + "ORDER BY tasks.id";
		ArrayList<Task> taskList = new ArrayList<>();

		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery();

			int id;
			String taskName;
			String taskDescription;
			LocalDate addDate;
			LocalDate finishDate;
			LocalDate deadline;
			LocalTime deadlineTime;

			while (resultSet.next()) {
				id = resultSet.getInt("id");
				taskName = resultSet.getString("task_name");
				taskDescription = resultSet.getString("task_description");
				addDate = resultSet.getDate("add_date").toLocalDate();

				try {
					///// checking if the task is finished - if not, it will be caught (deadline
					///// isn't important if it's already finished)
					finishDate = resultSet.getDate("finish_date").toLocalDate();
					taskList.add(new Task(id, taskName, taskDescription, addDate, finishDate));

				} catch (NullPointerException e) {

					try {
						deadline = resultSet.getDate("deadline").toLocalDate();
						deadlineTime = resultSet.getTime("deadline_time").toLocalTime();
						taskList.add(new Task(id, taskName, taskDescription, addDate, Deadline.yes, deadline.toString(),
								deadlineTime.toString()));

					} catch (NullPointerException exception) {
						taskList.add(new Task(id, taskName, taskDescription, addDate, Deadline.no, "", ""));
					}
				}
			}

			statement.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Cannot read the database.", "Error 005", JOptionPane.ERROR_MESSAGE);
		} finally {
			lock.unlock();
		}

		return taskList;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	protected void completeTask(int id) {
		lock.lock();

		String sql = "UPDATE task_dates " + "SET finish_date = ? " + "WHERE id = ?";

		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setDate(1, Date.valueOf(LocalDate.now().toString()));
			statement.setInt(2, id);
			statement.execute();

			statement.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Cannot update the database.", "Error 006", JOptionPane.ERROR_MESSAGE);
		} finally {
			lock.unlock();
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	protected void editTask(Task task) {
		lock.lock();

		String sqlTasks = "UPDATE tasks " + "SET task_name = ?, task_description = ? " + "WHERE id = ?";

		String sqlTaskDates = "UPDATE task_dates " + "SET deadline = ?, deadline_time = ? " + "WHERE id = ?";

		try {
			PreparedStatement taskStatement = connection.prepareStatement(sqlTasks);
			taskStatement.setString(1, task.getTaskName());
			taskStatement.setString(2, task.getTaskDescription());
			taskStatement.setInt(3, task.getId());
			taskStatement.execute();

			taskStatement.close();

			PreparedStatement taskDatesStatement = connection.prepareStatement(sqlTaskDates);

			if (task.getDeadlineStatus().equals(Deadline.yes)) {
				taskDatesStatement.setDate(1, Date.valueOf(task.getDeadline()));
				taskDatesStatement.setTime(2, Time.valueOf(task.getDeadlineTime()));
			} else {
				taskDatesStatement.setDate(1, null);
				taskDatesStatement.setTime(2, null);
			}
			taskDatesStatement.setInt(3, task.getId());
			taskDatesStatement.execute();

			taskDatesStatement.close();

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Cannot update the database.", "Error 007", JOptionPane.ERROR_MESSAGE);
		} finally {
			lock.unlock();
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	protected void deleteTask(int id) {
		lock.lock();

		String sqlDates = "DELETE FROM task_dates " + "WHERE id = ?";

		String sql = "DELETE FROM tasks " + "WHERE id = ?";

		try {
			PreparedStatement statementDates = connection.prepareStatement(sqlDates);
			statementDates.setInt(1, id);

			statementDates.execute();
			statementDates.close();

			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, id);

			statement.execute();
			statement.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Cannot update the database.", "Error 008", JOptionPane.ERROR_MESSAGE);
		} finally {
			lock.unlock();
		}

	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	protected void deleteAllTasks() {
		lock.lock();

		String sqlDates = "DELETE FROM task_dates";
		String sql = "DELETE FROM tasks";

		try {
			PreparedStatement statementDates = connection.prepareStatement(sqlDates);

			statementDates.execute();
			statementDates.close();

			PreparedStatement statement = connection.prepareStatement(sql);

			statement.execute();
			statement.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Cannot update the database.", "Error 009", JOptionPane.ERROR_MESSAGE);
		} finally {
			lock.unlock();
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	protected void disconnect() {

		try {
			connection.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Cannot disconnect from the database.", "Error 010", JOptionPane.ERROR_MESSAGE);
		}
	}
}
