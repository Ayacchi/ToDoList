package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import controller.Controller;
import model.Deadline;
import model.Task;

public class DeadlineDisplayPanel extends JPanel {
	private JLabel deadlineLabel;
	private JLabel deadlineDateLabel;
	private JLabel deadlineTimeLabel;
	private String taskDeadline;
	private String taskDeadlineTime;
	
	private JLabel addDateLabel = new JLabel("Added");
	private JLabel addDate;
	private JLabel finishDateLabel = new JLabel("Finished");
	private JLabel finishDate;
	
	private JPanel linePanel;

	protected DeadlineDisplayPanel(Controller controller, Task task, boolean showCompletedTasks, Color color) {
		
		setBackground(color);

		if (showCompletedTasks == false) {
			taskDeadline = task.getDeadline();
			taskDeadlineTime = task.getDeadlineTime();
			
			deadlineLabel = new JLabel("Deadline: ");

			if (task.getDeadlineStatus().equals(Deadline.no)) {
				linePanel = new JPanel() {
					@Override
					protected void paintComponent(Graphics g) {
						super.paintComponent(g);
						setBackground(color);
						g.drawLine(0, 5, 75, 5);
					}
				};
				
				linePanel.setPreferredSize(new Dimension(75, 10));
			} else {

				int year = Integer.parseInt(taskDeadline.substring(0, 4));
				int month = Integer.parseInt(taskDeadline.substring(5, 7));
				int day = Integer.parseInt(taskDeadline.substring(8));

				String[] monthList = { "January", "February", "March", "April", "May", "June", "July", "August",
						"September", "October", "November", "December" };
				StringBuilder sb = new StringBuilder();

				sb.append(day);
				sb.append(" ");
				sb.append(monthList[month - 1]);
				sb.append(" ");
				sb.append(year);

				deadlineDateLabel = new JLabel(sb.toString());
				deadlineTimeLabel = new JLabel(taskDeadlineTime.substring(0, 5));
			}
		} else {
			addDate = new JLabel(task.getAddDate().toString());
			finishDate = new JLabel(task.getFinishDate().toString());
		}

		Icon dropDownIcon = UIManager.getIcon("Table.descendingSortIcon");
		JButton dropDownButton = new JButton(dropDownIcon);
		dropDownButton.setBackground(color);
		dropDownButton.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
		dropDownButton.setPreferredSize(new Dimension(30, 10));

		JPopupMenu popupMenu = new JPopupMenu();
		
		if (showCompletedTasks == false) {
			JMenuItem editItem = new JMenuItem("Edit");

			editItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					if (MainFrame.getAddWindowsCount() == 0) {
						AddFrame editFrame = new AddFrame(controller, true, task);
						MainFrame.addWindowOpened();

					} else if (MainFrame.getAddWindowsCount() == 1) {
						// DO NOTHING
					}
				}
			});

			JMenuItem deleteItem = new JMenuItem("Delete");

			deleteItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int choice = JOptionPane.showConfirmDialog(DeadlineDisplayPanel.this,
							"Do you really want to delete the task?", "Confirmation", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {
						try {
							controller.connect();
							controller.deleteTask(task.getId());
						} finally {
							controller.disconnect();
						}
					}
				}
			});

			JMenuItem completeItem = new JMenuItem("Complete");

			completeItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						controller.connect();
						controller.completeTask(task.getId());
					} finally {
						controller.disconnect();
					}
				}
			});

			popupMenu.add(editItem);
			popupMenu.add(deleteItem);
			popupMenu.addSeparator();
			popupMenu.add(completeItem);
		}

		dropDownButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					popupMenu.show(dropDownButton, 0, 0);
				} catch (IllegalComponentStateException exception) {
					// do nothing - the user just missed the button, so no popup menu showing. And absolutely no errors because of that!
				}
			}
		});

		//////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////// Layout of DeadlineDisplayPanel
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		/////////////// First Row ///////////////////////////////////
		constraints.gridy = 0;

		constraints.gridx = 0;
		// no deadline for completed tasks ////////////////////////
		if (showCompletedTasks == false) {
			constraints.weightx = 1;
			constraints.weighty = 1;
			constraints.insets = new Insets(0, 0, 0, 0);
			constraints.anchor = GridBagConstraints.LINE_START;

			add(deadlineLabel, constraints);

			if (taskDeadline.equals("")) {
				constraints.gridx = 1;
				constraints.weightx = 40;
				constraints.weighty = 1;
				constraints.insets = new Insets(0, 10, 0, 0);
				constraints.anchor = GridBagConstraints.LINE_START;

				add(linePanel, constraints);
			} else {
				constraints.gridx = 1;
				constraints.weightx = 1;
				constraints.weighty = 1;
				constraints.anchor = GridBagConstraints.LINE_START;
				constraints.insets = new Insets(0, 10, 0, 0);

				add(deadlineDateLabel, constraints);

				constraints.gridx = 2;
				constraints.weightx = 40;
				constraints.weighty = 1;
				constraints.anchor = GridBagConstraints.LINE_START;
				constraints.insets = new Insets(0, 10, 0, 0);

				add(deadlineTimeLabel, constraints);
			}

			constraints.gridx++;
			constraints.weightx = 40;
			constraints.weighty = 1;
			constraints.anchor = GridBagConstraints.LINE_END;
			constraints.insets = new Insets(0, 0, 0, 30);
			
			add(dropDownButton, constraints);
		} else {
			//////// for completed tasks
			constraints.weightx = 1;
			constraints.weighty = 1;
			constraints.insets = new Insets(0, 0, 0, 0);
			constraints.anchor = GridBagConstraints.LINE_START;

			add(addDateLabel, constraints);
			
			constraints.gridx++;
			constraints.weightx = 30;
			constraints.weighty = 1;
			constraints.insets = new Insets(0, -5, 0, 0);
			constraints.anchor = GridBagConstraints.LINE_START;

			add(addDate, constraints);
			
			constraints.gridx++;
			constraints.weightx = 1;
			constraints.weighty = 1;
			constraints.insets = new Insets(0, 0, 0, 0);
			constraints.anchor = GridBagConstraints.CENTER;

			add(finishDateLabel, constraints);
			
			constraints.gridx++;
			constraints.weightx = 40;
			constraints.weighty = 1;
			constraints.insets = new Insets(0, 0, 0, 0);
			constraints.anchor = GridBagConstraints.LINE_START;

			add(finishDate, constraints);
		}
	}
}
