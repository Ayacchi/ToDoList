package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.UIManager;

import controller.Controller;
import controller.DatabaseActionListener;

public class MainFrame extends JFrame {

	private MainPanel mainPanel;
	private Controller controller;
	private AddFirstActionListener addFirstActionListener;
	private JScrollPane mainPanelScrollPane;
	private JFileChooser fileChooser;

	private static int addWindowsCount = 0;

	public MainFrame() {
		super("TO DO List");

		setLayout(new BorderLayout());

		controller = new Controller();

		controller.setEventListener(new DatabaseActionListener() {
			@Override
			public void databaseEventOccured() {
				revalidate();
				mainPanel.refresh(controller);
				mainPanel.repaint();
			}
		});

		UIManager.put("ToolTip.background", Color.WHITE);

		mainPanel = new MainPanel(controller);
		addFirstActionListener = new AddFirstActionListener();
		addFirstActionListener.setController(controller);

		mainPanelScrollPane = new JScrollPane(mainPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		add(mainPanelScrollPane, BorderLayout.CENTER);

		fileChooser = new JFileChooser();
		TaskFileFilter taskFileFilter = new TaskFileFilter();
		fileChooser.addChoosableFileFilter(taskFileFilter);
		fileChooser.setFileFilter(taskFileFilter);
		fileChooser.setAcceptAllFileFilterUsed(false);

		setJMenuBar(createMenuBar());

		setSize(1000, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		setMinimumSize(new Dimension(1000, 400));
		setResizable(true);

		ActionListener autoRefreshListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainPanel.refresh(controller);
			}
		};

		Timer timer = new Timer(10000, autoRefreshListener);
		timer.setRepeats(true);
		timer.start();

	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////// MENU
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		JMenuItem addNewTaskItem = new JMenuItem("Add Task");

		JMenuItem importItem = new JMenuItem("Import...");

		JMenuItem exportItem = new JMenuItem("Export...");

		JMenuItem resetItem = new JMenuItem("Reset");

		JMenuItem exitItem = new JMenuItem("Exit");

		fileMenu.add(addNewTaskItem);
		fileMenu.addSeparator();
		fileMenu.add(importItem);
		fileMenu.add(exportItem);
		fileMenu.add(resetItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);

		addNewTaskItem.addActionListener(addFirstActionListener);

		importItem.setToolTipText("Imports previously exported data");
		exportItem.setToolTipText(
				"Exports the tasks' data - useful, if you want to continue using the program on another device");
		resetItem.setToolTipText("Clears ALL data - the reset is irreversible!");

		importItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(MainFrame.this,
						"Imported data will overwrite the current database. Do you want to do it?", "Import",
						JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
						try {
							controller.connect();
							controller.loadFromFile(fileChooser.getSelectedFile());
							// afterwards mainPanel needs to refresh, but it's already done via
							// DatabaseActionListener
						} finally {
							controller.disconnect();
						}
					}
				}
			}
		});

		exportItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {

					File file = fileChooser.getSelectedFile();
					String fileName = file.getName();
					int extensionIndex = fileName.lastIndexOf(".");

					if (extensionIndex == fileName.length() - 1) {
						file = new File(file.getPath() + "tsk");
					} else if (extensionIndex == fileName.length() - 4
							&& fileName.substring(fileName.length() - 3).equals("tsk")) {
						// do nothing - the file extension is ok
					} else {
						file = new File(file.getPath() + ".tsk");
					}

					try {
						controller.connect();
						controller.saveToFile(file);
					} finally {
						controller.disconnect();
					}
				}
			}
		});

		resetItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(MainFrame.this,
						"Do you really want to reset ALL tasks? This operation is irreversible.", "Reset",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

				if (choice == JOptionPane.YES_OPTION) {
					try {
						controller.connect();
						controller.deleteAllTasks();
					} finally {
						controller.disconnect();
					}
				}
			}
		});

		JMenu windowMenu = new JMenu("Window");
		JMenu viewMenu = new JMenu("View");
		JRadioButtonMenuItem activeTasksItem = new JRadioButtonMenuItem("Active Tasks");
		JRadioButtonMenuItem doneTasksItem = new JRadioButtonMenuItem("Completed Tasks");

		activeTasksItem.setSelected(true);

		activeTasksItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (activeTasksItem.isSelected() == true) {
					doneTasksItem.setSelected(false);

					mainPanel.setShowCompletedTasks(false);
					mainPanel.changeView(controller);
					setTitle("TO DO List");
				} else {
					activeTasksItem.setSelected(true); // disables deselecting
				}
			}
		});

		doneTasksItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (doneTasksItem.isSelected() == true) {
					activeTasksItem.setSelected(false);

					mainPanel.setShowCompletedTasks(true);
					mainPanel.changeView(controller);
					setTitle("TO DO List - Completed Tasks");
				} else {
					doneTasksItem.setSelected(true); // disables deselecting
				}
			}
		});

		if (activeTasksItem.isSelected() == true) {
			doneTasksItem.setSelected(false);
		}

		if (doneTasksItem.isSelected() == true) {
			activeTasksItem.setSelected(false);
		}

		windowMenu.add(viewMenu);
		viewMenu.add(activeTasksItem);
		viewMenu.add(doneTasksItem);

		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutItem = new JMenuItem("About");

		aboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AboutFrame();
			}

		});

		helpMenu.add(aboutItem);

		menuBar.add(fileMenu);
		menuBar.add(windowMenu);
		menuBar.add(helpMenu);

		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(MainFrame.this,
						"Do you really want to exit the application?", "Exit", JOptionPane.YES_NO_OPTION);

				if (choice == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});

		fileMenu.setMnemonic(KeyEvent.VK_F);
		windowMenu.setMnemonic(KeyEvent.VK_W);
		helpMenu.setMnemonic(KeyEvent.VK_H);

		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		addNewTaskItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.SHIFT_MASK));

		return menuBar;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static int getAddWindowsCount() {
		return addWindowsCount;
	}

	protected static void addWindowOpened() {
		addWindowsCount++;
	}

	protected static void addWindowClosed() {
		addWindowsCount--;
	}
}
