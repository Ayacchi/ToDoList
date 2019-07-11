package view;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class TaskDescriptionPanel extends JPanel {
	private JTextArea descriptionArea;
	private String descriptionText;
	
	protected TaskDescriptionPanel() {
		descriptionArea = new JTextArea(5,38);
		
		descriptionArea.setVisible(true);
		descriptionArea.setLineWrap(true);
		descriptionArea.setWrapStyleWord(true);
		
		//descriptionArea.setLayout(new BorderLayout());
		setLayout(new BorderLayout());
		
		add(new JScrollPane(descriptionArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
	}
	
	protected String getDescription() {
		
		try {
			descriptionText = descriptionArea.getText();
		} catch(NullPointerException e) {
			descriptionText = null;
		}
		
		return descriptionText;
	}
	
	protected void setDescription(String text) {
		descriptionArea.setText(text);
	}
}
