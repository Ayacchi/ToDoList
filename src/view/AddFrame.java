package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import controller.Controller;
import model.Deadline;
import model.Task;

public class AddFrame extends JFrame {
	private JLabel taskNameLabel;
	private JTextField taskNameField;
	private JLabel taskDescriptionLabel;
	private TaskDescriptionPanel taskDescriptionPanel;
	private JButton addButton;
	private JCheckBox deadlineCheckBox;
	private boolean deadlineChecked;
	private DeadlinePanel deadlinePanel;
	private Deadline isWithDeadline;

	protected AddFrame(Controller controller, boolean isEditFrame, Task task) {
		super("Add Task");

		if (isEditFrame == true) {
			setTitle("Edit Task");
		}

		setSize(600, 260);
		setVisible(true);
		setResizable(false);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				MainFrame.addWindowClosed();
			}
		});

		taskNameLabel = new JLabel("Task: ");
		taskNameField = new JTextField(30);
		taskDescriptionLabel = new JLabel("Description (Optional): ");
		taskDescriptionPanel = new TaskDescriptionPanel();

		if (isEditFrame == false) {
			deadlineCheckBox = new JCheckBox("Deadline", false);
			deadlineChecked = false;

			addButton = new JButton("Add");
		} else {
			taskNameField.setText(task.getTaskName());
			taskDescriptionPanel.setDescription(task.getTaskDescription());

			if (task.getDeadlineStatus().equals(Deadline.no)) {
				deadlineChecked = false;
			} else {
				deadlineChecked = true;
			}
			deadlineCheckBox = new JCheckBox("Deadline", deadlineChecked);

			addButton = new JButton("Submit");
		}

		/////////////// deadlinePanel ///////////////////////////////////////
		deadlinePanel = new DeadlinePanel(deadlineChecked, isEditFrame, task);

		deadlineCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deadlineChecked = deadlineCheckBox.isSelected();

				deadlinePanel.deadlinePanelSetup(deadlineChecked, isEditFrame, task);

				deadlinePanel.revalidate();
				deadlinePanel.repaint();
			}

		});
		
		/////////////////////////////////////////////////////////////////////////////
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (!isTaskFieldEmpty()) {

					if (isTaskNameTooLong()) {
						JOptionPane.showMessageDialog(AddFrame.this, "The task title is too long.", "Too Long",
								JOptionPane.ERROR_MESSAGE);
					} else if (isTaskDescriptionTooLong()) {
						JOptionPane.showMessageDialog(AddFrame.this, "The task description is too long.", "Too Long",
								JOptionPane.ERROR_MESSAGE);
					} else {
						////// if everything is correct:
						Task newTask;

						if (deadlineChecked == true) {
							isWithDeadline = Deadline.yes;
						} else {
							isWithDeadline = Deadline.no;
						}
						
						//////// if deadline is too early ///////////////////
						if (deadlineChecked == true && isDeadlineTooEarly(deadlinePanel.getDeadline(deadlineChecked), deadlinePanel.getDeadlineTime(deadlineChecked)) ) {
							int choice = JOptionPane.showConfirmDialog(AddFrame.this,
									"It is already past the deadline you set. Are you sure that's what you want?", "Impossible Deadline", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
							
							if (choice == JOptionPane.NO_OPTION) {
								return;
							}
						}
						/////////////////////////////////////////////////////////
						
						try {
							controller.connect();

							if (isEditFrame == false) {
								newTask = new Task(taskNameField.getText(), taskDescriptionPanel.getDescription(),
										isWithDeadline, deadlinePanel.getDeadline(deadlineChecked),
										deadlinePanel.getDeadlineTime(deadlineChecked));
								newTask.setAddDate(LocalDate.now());
								controller.addNewTask(newTask);
							} else {
								newTask = new Task(task.getId(), taskNameField.getText(),
										taskDescriptionPanel.getDescription(), isWithDeadline,
										deadlinePanel.getDeadline(deadlineChecked),
										deadlinePanel.getDeadlineTime(deadlineChecked));
								controller.editTask(newTask);
							}
						} finally {
							controller.disconnect();
						}

						MainFrame.addWindowClosed();
						dispose();
					}

				} else {
					JOptionPane.showMessageDialog(AddFrame.this, "Please type the task", "Error",
							JOptionPane.OK_OPTION | JOptionPane.INFORMATION_MESSAGE);
				}
			}

		});
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////////////// Layout /////////////////////////////////////////////////////////////////////////////////////////
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		////////////////// First Row //////////////////////
		constraints.gridy = 0;

		constraints.gridx = 0;
		constraints.weighty = 1;
		constraints.weightx = 1;
		constraints.insets = new Insets(2, 100, 0, 0);
		constraints.anchor = GridBagConstraints.LINE_START;

		add(taskNameLabel, constraints);

		constraints.gridx = 1;
		constraints.weighty = 1;
		constraints.weightx = 10;
		constraints.insets = new Insets(5, 6, 0, 5);
		constraints.fill = GridBagConstraints.HORIZONTAL;

		add(taskNameField, constraints);

		////////////////// Second Row //////////////////////
		constraints.gridy = 1;

		constraints.gridx = 0;
		constraints.weighty = 1;
		constraints.weightx = 1;
		constraints.insets = new Insets(0, 5, 65, 0);
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;

		add(taskDescriptionLabel, constraints);

		constraints.gridx = 1;
		constraints.weighty = 1;
		constraints.weightx = 10;
		constraints.insets = new Insets(5, 6, 0, 5);
		constraints.fill = GridBagConstraints.BOTH;

		add(taskDescriptionPanel, constraints);

		////////////////// Third Row //////////////////////
		constraints.gridy = 2;

		constraints.gridx = 0;
		constraints.weighty = 1;
		constraints.weightx = 1;
		constraints.insets = new Insets(10, 0, 15, 0);
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.CENTER;

		add(deadlineCheckBox, constraints);

		constraints.gridx = 1;
		constraints.weighty = 1;
		constraints.weightx = 10;
		constraints.insets = new Insets(0, 0, -5, 0);
		constraints.anchor = GridBagConstraints.CENTER;

		add(deadlinePanel, constraints);

		////////////////// Fourth Row //////////////////////
		constraints.gridy = 3;

		constraints.gridx = 1;
		constraints.weighty = 1;
		constraints.weightx = 10;
		constraints.insets = new Insets(0, 0, 15, 150);
		constraints.anchor = GridBagConstraints.CENTER;

		add(addButton, constraints);

	}

	///////////// Various checks - before submitting the task //////////////////////////////////////////
	private boolean isTaskFieldEmpty() {
		if (taskNameField.getText().equals("")) {
			return true;
		}
		if (taskNameField.getText().indexOf(" ") == 0) {
			return true;
		}

		return false;
	}

	private boolean isTaskNameTooLong() {
		if (taskNameField.getText().length() > 99) {
			return true;
		}

		return false;
	}

	private boolean isTaskDescriptionTooLong() {
		if (taskDescriptionPanel.getDescription().length() > 999) {
			return true;
		}

		return false;
	}
	
	private boolean isDeadlineTooEarly(String deadline, String deadlineTime) {
		int compareDate = LocalDate.parse( deadline ).compareTo(LocalDate.now());
		int compareTime = LocalTime.parse( deadlineTime ).compareTo(LocalTime.now());
		
		if (compareDate <= 0 && compareTime <= 0) {
			return true;
		}
		return false;
	}
	
}
