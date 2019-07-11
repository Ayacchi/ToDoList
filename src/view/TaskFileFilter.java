package view;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class TaskFileFilter extends FileFilter {

	@Override
	public boolean accept(File file) {
		String name = file.getName();
		int extensionIndex = name.lastIndexOf(".");
		
		if (file.isDirectory()) {
			return true; // enables browsing through directories
		}
		
		if (extensionIndex == -1 || extensionIndex == (name.length()-1)) {
			return false; // there's no extension (no dot or dot at the end)
		}
		
		String extension = name.substring(extensionIndex+1, name.length());
		
		if (extension.equals("tsk")) {
			return true;
		}
		
		// else
		return false;
	}

	@Override
	public String getDescription() {
		return "Task database files (*.tsk)";
	}

}
