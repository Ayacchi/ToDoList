<b>Simple desktop program for managing task lists. To use it just download ToDoList.jar file and make sure you have at least Java 8 installed. </b>

Main features:
- Adding tasks with or without deadline
- Editing or deleting already added tasks
- Color notifications when the deadline is exceeded (in real-time - it automatically refreshes)
- Setting tasks as completed
- Viewable active and completed task lists
- Import and export functions allowing data transfer between workstations, as well as managing multiple tasklists (if there's a need for that for some reason)
- Reset function erasing all tasks, both active and completed
<br>
<hr>
<br>
Tasks are stored in a local H2 SQL database located in user's documents folder. The database is created during the first run of the program. If you want to use the code, you need a proper h2 library (at least v.1.4.197). The main method is in App.java file located in view package.
