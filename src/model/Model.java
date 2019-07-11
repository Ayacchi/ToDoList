package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class Model {

	private volatile boolean dbTablesExist = false;
	private static volatile boolean dbTablesCreated = false;

	private Database database;

	public Model() {
		database = new Database();

		if (dbTablesCreated == false) {
			dbTablesExist = database.testDBTables();

			if (dbTablesExist == false) {
				database.deleteEmptyTables();
				database.createTables();
				dbTablesCreated = true;
			}
		}
		database.disconnect();
	}

	public void deleteEmptyTables() {
		database.deleteEmptyTables();
	}

	public void addNewTask(Task task) {
		database.addNewTask(task);
	}

	public void editTask(Task task) {
		database.editTask(task);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////// Not finished Tasks ////////////////////////////////
	public ArrayList<Task> loadAllNotFinishedTasks() {
		return loadAllTasks(IsDone.no);
	}

	//////////////////////// Finished Tasks /////////////////////////////////////
	public ArrayList<Task> loadAllFinishedTasks() {
		return loadAllTasks(IsDone.yes);
	}

	///////////////////////////////////////////////////////////////////////////////////
	private ArrayList<Task> loadAllTasks(IsDone isDone) {
		ArrayList<Task> fullTaskList = database.getAllTasks();
		ArrayList<Task> taskList = new ArrayList<Task>();

		for (Task task : fullTaskList) {
			if (task.getTaskStatus().equals(isDone)) {
				taskList.add(task);
			}
		}

		return taskList;
	}

	private ArrayList<Task> loadAllTasks() {
		return database.getAllTasks();
	}

	////////////////////////////// Counting tasks
	////////////////////////////// ////////////////////////////////////////////////////////
	public int countNotFinishedTasks() {
		return loadAllNotFinishedTasks().size();
	}

	public int countFinishedTasks() {
		return loadAllFinishedTasks().size();
	}

	/////////////////////////////// Deleting tasks
	/////////////////////////////// /////////////////////////////////////////////////////////////
	public void deleteTask(int id) {
		database.deleteTask(id);
	}

	public void deleteAllTasks() {
		database.deleteAllTasks();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void completeTask(int id) {
		database.completeTask(id);
	}

	public void connect() {
		database.connect();
	}

	public void disconnect() {
		database.disconnect();
	}

	///////////////////////////// Import & Export
	///////////////////////////// /////////////////////////////////////////////////////////////////////////
	public void saveToFile(File file) {
		ArrayList<Task> taskList = loadAllTasks();
		Task[] tasks = taskList.toArray(new Task[taskList.size()]);

		FileOutputStream foStream;
		ObjectOutputStream ooStream;

		try {
			foStream = new FileOutputStream(file);
			ooStream = new ObjectOutputStream(foStream);

			ooStream.writeObject(tasks);
			ooStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadFromFile(File file) {
		ArrayList<Task> taskList = new ArrayList<Task>();

		FileInputStream fiStream;
		ObjectInputStream oiStream;

		try {
			fiStream = new FileInputStream(file);
			oiStream = new ObjectInputStream(fiStream);

			Task[] tasks = (Task[]) oiStream.readObject();
			taskList.addAll(Arrays.asList(tasks));

			oiStream.close();

			for (Task task : tasks) {
				database.addNewTask(task);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
}
