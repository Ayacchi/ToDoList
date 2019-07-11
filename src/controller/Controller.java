package controller;

import java.io.File;
import java.util.ArrayList;

import model.Model;
import model.Task;

public class Controller{

	private Model model;
	
	private DatabaseActionListener databaseChangeListener;

	public Controller() {
		model = new Model();
	}

	public void addNewTask(Task task) {
		model.addNewTask(task);
		
		if (databaseChangeListener != null) {
			///// only notification is needed - for MainFrame to repaint
			databaseChangeListener.databaseEventOccured();
		}
	}

	public void setEventListener(DatabaseActionListener listener) {
		databaseChangeListener = listener;
	}
	
	public void editTask(Task task) {
		model.editTask(task);
		
		if (databaseChangeListener != null) {
			///// only notification is needed - for MainFrame to repaint
			databaseChangeListener.databaseEventOccured();
		}
	}

	///////////////// connections //////////////////////////////////////////
	public void connect() {
		model.connect();
	}

	public void disconnect() {
		model.disconnect();
	}

	//////////////////// Not finished Tasks ////////////////////////////////
	public ArrayList<Task> loadAllNotFinishedTasks() {
		return model.loadAllNotFinishedTasks();
	}
	
//////////////////////// Finished Tasks /////////////////////////////////////
	public ArrayList<Task> loadAllFinishedTasks() {
		return model.loadAllFinishedTasks();
	}
	
	////////////////////// Deleting /////////////////////////////////////////////
	public void deleteEmptyTables() {
		model.deleteEmptyTables();
	}
	
	public void deleteTask(int id) {
		model.deleteTask(id);
		
		if (databaseChangeListener != null) {
			///// only notification is needed - for MainFrame to repaint
			databaseChangeListener.databaseEventOccured();
		}
	}
	
	public void deleteAllTasks() {
		model.deleteAllTasks();
		
		if (databaseChangeListener != null) {
			///// only notification is needed - for MainFrame to repaint
			databaseChangeListener.databaseEventOccured();
		}
	}
	
	//////////////////// Finishing tasks /////////////////////////////
	public void completeTask(int id) {
		model.completeTask(id);
		
		if (databaseChangeListener != null) {
			///// only notification is needed - for MainFrame to repaint
			databaseChangeListener.databaseEventOccured();
		}
	}
	
	/////////////////// Import & Export ///////////////////////////////
	public void saveToFile(File file) {
		model.saveToFile(file);
	}
	
	public void loadFromFile(File file) {
		model.deleteAllTasks();
		model.loadFromFile(file);
		
		if (databaseChangeListener != null) {
			///// only notification is needed - for MainFrame to repaint
			databaseChangeListener.databaseEventOccured();
		}
	}
}
