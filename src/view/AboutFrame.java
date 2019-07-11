package view;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class AboutFrame extends JFrame {
	private JEditorPane editorPane;
	private JEditorPane editorPane2;
	private String aboutText;
	private String aboutText2;

	protected AboutFrame() {
		super("About");

		setSize(325, 240);
		setVisible(true);
		setResizable(false);

		aboutText = "This program was created essentially for learning purposes. It is provided as is under GNU General Public License v3.0, without warranty of any kind. "
				+ "It should work fine on any system, but if you find a bug, need help or just want to send me some feedback, "
				+ "please <a href=\'mailto:aleksandra.henig@gmail.com'>contact me</a>.";
		aboutText2 = "The source code along with the license file can be found <a href=\'https://github.com/Ayacchi/ToDoList'>here</a>.";

		editorPane = new JEditorPane();

		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		editorPane.setText(aboutText);
		editorPane.setOpaque(false);

		editorPane.addMouseListener(new HyperlinkMouseListener() {
			@Override
			protected void copyLink(String href) {
				super.copyLink("aleksandra.henig@gmail.com");
			}
		});
		
		editorPane2 = new JEditorPane();

		editorPane2.setEditable(false);
		editorPane2.setContentType("text/html");
		editorPane2.setText(aboutText2);
		editorPane2.setOpaque(false);
		
		editorPane2.addMouseListener(new HyperlinkMouseListener());
		
		///////////////////////////////// LAYOUT
		///////////////////////////////////////////////////////////////////////////////
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridy = 0;

		constraints.gridx = 0;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(12, 15, 0, 12);

		add(editorPane, constraints);

		constraints.gridy++;

		constraints.gridx = 0;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(0, 15, 10, 12);

		add(editorPane2, constraints);
	}
}
