package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import controller.Controller;

public class AddFirstActionListener implements ActionListener {
	private AddFrame addFrame;
	private Controller controller;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (MainFrame.getAddWindowsCount() == 0) {
			addFrame = new AddFrame(controller, false, null);
			MainFrame.addWindowOpened();
			
		} else if(MainFrame.getAddWindowsCount() == 1) {
			// DO NOTHING
		}
	}
	
	protected void setController(Controller controller ) {
		this.controller = controller;
	}
}
