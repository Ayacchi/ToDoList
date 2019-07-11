package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import controller.Controller;
import model.Deadline;
import model.Task;

public class TaskPanel extends JPanel {

	private JLabel nameLabel;
	private JTextArea descriptionArea;
	private DeadlineDisplayPanel deadlineDisplayPanel;
	private JScrollPane descriptionScrollPane;
	private Font nameLabelFont;

	public TaskPanel(Controller controller, Task task, boolean showCompletedTasks, int count) {
		setBorder(BorderFactory.createEtchedBorder());

		setPreferredSize(new Dimension(1000, 105));

		setMinimumSize(new Dimension(800, 105));
		setMaximumSize(new Dimension(5000, 105));

		nameLabel = new JLabel(task.getTaskName());
		descriptionArea = new JTextArea(task.getTaskDescription(), 3, 20);

		Color color;

		// grey - normally
		if (count % 2 == 1) {
			color = new Color(247, 247, 247);
		} else {
			color = new Color(251, 251, 251);
		}
		if (showCompletedTasks == false) {
			if (task.getDeadlineStatus().equals(Deadline.yes)) {
				int compareDate = LocalDate.parse(task.getDeadline()).compareTo(LocalDate.now());
				int compareTime = LocalTime.parse(task.getDeadlineTime()).compareTo(LocalTime.now());

				if ((compareDate == 0 && compareTime <= 0) || compareDate < 0) {
					// red
					// if (count % 2 == 1) {
					// color = new Color(254, 197, 169);
					// } else {
					color = new Color(255, 186, 174);
					// }
					descriptionArea.setBorder(BorderFactory.createLineBorder(new Color(253, 153, 128)));
				}
				// if it's not past deadline, colors will stay normal, as they are
			}
		}

		setBackground(color);

		///////////////////////////
		deadlineDisplayPanel = new DeadlineDisplayPanel(controller, task, showCompletedTasks, color);

		descriptionScrollPane = new JScrollPane(descriptionArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		nameLabelFont = new Font(nameLabel.getFont().getFontName(), nameLabel.getFont().getStyle(), 16);
		nameLabel.setFont(nameLabelFont);

		descriptionScrollPane.setBackground(color);
		descriptionArea.setBackground(color);
		descriptionScrollPane.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		descriptionArea.setLineWrap(true);
		descriptionArea.setWrapStyleWord(true);
		descriptionArea.setEditable(false);

		////////////////////////////
		if (showCompletedTasks == false) {
			String tooltip = "Added " + task.getAddDate();
			setToolTipText(tooltip);
			descriptionArea.setToolTipText(tooltip);
		}

		//////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////// Layout
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		/////////////// First Row //////////////////////////////////
		constraints.gridy = 0;

		constraints.gridx = 0;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);

		add(nameLabel, constraints);

		/////////////// Second Row ////////////////////////////////
		constraints.gridy++;

		constraints.gridx = 0;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 0, 0, 0);

		add(descriptionScrollPane, constraints);

		////////////// Third Row ///////////////////////////////////

		constraints.gridy++;

		constraints.gridx = 0;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.LINE_START;

		add(deadlineDisplayPanel, constraints);

	}
}
