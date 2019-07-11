package view;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Deadline;
import model.Task;

public class DeadlinePanel extends JPanel {
	
	private JLabel timeSeparatorLabel = new JLabel(":");
	
	private JComboBox<String> dayBox;
	private JComboBox<String> monthBox;
	private JComboBox<Integer> yearBox;
	private JComboBox<String> hourBox;
	private JComboBox<String> minuteBox;
	
	protected DeadlinePanel(boolean deadlineChecked, boolean isEditFrame, Task task) {
		
		Font font = new Font(timeSeparatorLabel.getFont().getFontName(), timeSeparatorLabel.getFont().getStyle(), 18);
		timeSeparatorLabel.setFont(font);
		
		yearBox = new JComboBox<Integer>();
		for (int i=2019; i<2051; i++) {
			yearBox.addItem(i);
		}
		
		yearBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dayBox.removeAllItems();
				dayBoxSetup();
			}
		});
		
		DefaultListCellRenderer renderer = new DefaultListCellRenderer();
		renderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
		
		String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		monthBox = new JComboBox<String>(months);
		monthBox.setRenderer(renderer);
		
		monthBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dayBox.removeAllItems();
				dayBoxSetup();
			}
		});
		
		dayBox = new JComboBox<String>();
		
		dayBoxSetup();
		
		hourBox = new JComboBox<String>();
		for (int i=0; i<24; i++) {
			String hour = String.format("%02d", i);
			hourBox.addItem(hour);
		}
		
		minuteBox = new JComboBox<String>();
		for (int i=0; i<60; i++) {
			String minute = String.format("%02d", i);
			minuteBox.addItem(minute);
		}
		
		//////////////////////////////////////////////////////
		if (isEditFrame == true && deadlineChecked == true) {
			String deadlineDate = task.getDeadline();
			String deadlineTime = task.getDeadlineTime();
			
			selectDateAndTime(deadlineDate, deadlineTime);
		}
		
		setLayout(new GridBagLayout());
		
		deadlinePanelSetup(deadlineChecked, isEditFrame, task);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	protected String getDeadline(boolean deadlineChecked) {
		
		if (deadlineChecked == false) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(yearBox.getSelectedItem().toString());
		sb.append("-");
		sb.append(String.format("%02d", monthBox.getSelectedIndex()+1));
		sb.append("-");
		sb.append(dayBox.getSelectedItem().toString());
		
		return sb.toString();
	}
	
	protected String getDeadlineTime(boolean deadlineChecked) {
		if (deadlineChecked == false) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(hourBox.getSelectedItem().toString());
		sb.append(":");
		sb.append(minuteBox.getSelectedItem().toString());
		sb.append(":00");
		
		return sb.toString();
	}
	
	private void dayBoxSetup() {
		int iMax;
		
		if (monthBox.getSelectedItem().equals("February") && (int)yearBox.getSelectedItem()%4 == 0) {
			iMax = 30;
		} else if (monthBox.getSelectedItem().equals("February") && (int)yearBox.getSelectedItem()%4 != 0) {
			iMax = 29;
		} else if (monthBox.getSelectedItem().equals("April") || monthBox.getSelectedItem().equals("June") || monthBox.getSelectedItem().equals("September") || monthBox.getSelectedItem().equals("November")) {
			iMax = 31;
		} else {
			iMax = 32;
		}
		
		for (int i=1; i<iMax; i++) {
			String day = String.format("%02d", i);
			dayBox.addItem(day);
		}
	}
	
	protected void deadlinePanelSetup(boolean deadlineChecked, boolean isEditFrame, Task task) {

		GridBagConstraints constraints = new GridBagConstraints();
		
		if (deadlineChecked == false) {
			removeAll();
			
		} else {
			removeAll();
			
			if (isEditFrame == false || (isEditFrame == true && task.getDeadlineStatus().equals(Deadline.no) && deadlineChecked == true) ) {
				String todaysDate = LocalDate.now().toString();
				String todaysTime = LocalTime.now().toString();
				
				selectDateAndTime(todaysDate, todaysTime);
			}
			
			constraints.gridy = 0;
			
			constraints.gridx = 0;
			constraints.weighty = 1;
			constraints.weightx = 1;
			constraints.insets = new Insets(0,0,10,0);
			
			add(yearBox, constraints);
			
			constraints.gridx++;
			constraints.weighty = 1;
			constraints.weightx = 1;
			constraints.insets = new Insets(0,0,10,0);
			
			add(monthBox, constraints);
			
			constraints.gridx++;
			constraints.weighty = 1;
			constraints.weightx = 1;
			constraints.insets = new Insets(0,0,10,60);
			
			add(dayBox, constraints);
			
			constraints.gridx++;
			constraints.weighty = 1;
			constraints.weightx = 1;
			constraints.insets = new Insets(0,-30,10,30);
			
			add(hourBox, constraints);
			
			constraints.gridx++;
			constraints.weighty = 1;
			constraints.weightx = 1;
			constraints.insets = new Insets(0,-30,10,30);
			
			add(timeSeparatorLabel, constraints);
			
			constraints.gridx++;
			constraints.weighty = 1;
			constraints.weightx = 1;
			constraints.insets = new Insets(0,-30,10,30);
			
			add(minuteBox, constraints);
		}
	}
	
	private void selectDateAndTime(String date, String time) {
		yearBox.setSelectedItem(Integer.parseInt(date.substring(0, 4)));
		monthBox.setSelectedIndex(Integer.parseInt(date.substring(5, 7)) - 1);
		
		//// to make days display properly for the previously picked month
		dayBox.removeAllItems();
		dayBoxSetup();
		
		dayBox.setSelectedIndex(Integer.parseInt(date.substring(8)) - 1); // days start with 1
		
		hourBox.setSelectedIndex(Integer.parseInt(time.substring(0, 2))); // hours and minutes start with 00
		minuteBox.setSelectedIndex(Integer.parseInt(time.substring(3, 5)));
	}
}
