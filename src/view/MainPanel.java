package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.Controller;
import model.Task;

public class MainPanel extends JPanel {

	private JLabel theresNothingLabel;
	private JLabel noCompletedTasksLabel = new JLabel("There aren't any completed tasks.");

	private ArrayList<Task> activeTaskList;
	private ArrayList<Task> finishedTaskList;
	private boolean showCompletedTasks;
	
	public MainPanel(Controller controller) {
		showCompletedTasks = false;

		theresNothingLabel = new JLabel("There's nothing here. Let's add something to do!");
		theresNothingLabel.setFont(getFont().deriveFont(32f));

		noCompletedTasksLabel.setFont(getFont().deriveFont(32f));
		
		try {
			controller.connect();
			activeTaskList = controller.loadAllNotFinishedTasks();
			finishedTaskList = controller.loadAllFinishedTasks();
		} finally {
			controller.disconnect();
		}

		setGridLayoutComponents(controller, activeTaskList, showCompletedTasks);
	}

	///////////////////////// LAYOUT //////////////////////////////////////////////////////////////////////////////////
	private void setGridLayoutComponents(Controller controller, ArrayList<Task> taskList, boolean showCompletedTasks) {

		if (taskList.size() == 0) {

			if (showCompletedTasks == false) {
				setToolTipText("To add a task, click File->Add Task or press Shift+A");
			}

			setLayout(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();

			/////////////////// First Row ///////////////////////

			constraints.gridy = 0;

			constraints.gridx = 0;
			constraints.weightx = 1;
			constraints.weighty = 8;
			constraints.anchor = GridBagConstraints.CENTER;
			constraints.insets = new Insets(0, 0, 400, 0);

			if (showCompletedTasks == false) {
				add(theresNothingLabel, constraints);
			} else {
				add(noCompletedTasksLabel, constraints);
			}

		} else if (taskList.size() > 0) {
			
			setToolTipText(null); // removes previously set tooltip (otherwise it stayed on the panel even after adding tasks, if there weren't any tasks at the start of the program)
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			for (int i = 0; i < taskList.size(); i++) {
				add(new TaskPanel(controller, taskList.get(i), showCompletedTasks, i));
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	protected void refresh(Controller controller) {
		activeTaskList.clear();
		finishedTaskList.clear();

		try {
			controller.connect();
			activeTaskList = controller.loadAllNotFinishedTasks();
			finishedTaskList = controller.loadAllFinishedTasks();
		} finally {
			controller.disconnect();
		}

		changeView(controller);
	}

	// for changing between active and finished tasks:
	protected void changeView(Controller controller) {
		removeAll();

		if (showCompletedTasks == false) {
			setGridLayoutComponents(controller, activeTaskList, showCompletedTasks);
		} else {
			setGridLayoutComponents(controller, finishedTaskList, showCompletedTasks);
		}

		revalidate();
		repaint();
	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	protected void setShowCompletedTasks(boolean showCompletedTasks) {
		this.showCompletedTasks = showCompletedTasks;
	}
}
