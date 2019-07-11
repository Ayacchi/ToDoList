package controller;

import java.util.EventListener;

public interface DatabaseActionListener extends EventListener {
	public void databaseEventOccured();
}
