package model;

import java.io.Serializable;
import java.time.LocalDate;

public class Task implements Serializable {
	
	private static final long serialVersionUID = -6593531790460169356L;
	
	private int id;
	private String taskName;
	private String taskDescription;
	private LocalDate addDate;
	private IsDone isDone;
	private LocalDate finishDate;
	private Deadline withDeadline;
	private String deadline;
	private String deadlineTime;
	
	////////////////// Constructors for added tasks ////////////////////////////////////
	
	public Task(String taskName, String taskDescription) {
		this.taskName = taskName;
		this.taskDescription = taskDescription;
	}
	
	////////////////// Constructors for added tasks with deadline //////////////////////
	public Task(int id, String taskName, String taskDescription, LocalDate addDate, Deadline withDeadline, String deadline, String deadlineTime) {
		this.id = id;
		this.taskName = taskName;
		this.taskDescription = taskDescription;
		this.addDate = addDate;
		this.deadline = deadline;
		this.deadlineTime = deadlineTime;
		this.isDone = IsDone.no;
		this.withDeadline = withDeadline;
		
	}
	
	public Task(String taskName, String taskDescription, Deadline withDeadline, String deadline, String deadlineTime) {
		this.taskName = taskName;
		this.taskDescription = taskDescription;
		this.addDate = addDate;
		this.deadline = deadline;
		this.deadlineTime = deadlineTime;
		this.isDone = IsDone.no;
		this.withDeadline = withDeadline;
	}
	
	public Task(int id, String taskName, String taskDescription, Deadline withDeadline, String deadline, String deadlineTime) {
		this.id = id;
		this.taskName = taskName;
		this.taskDescription = taskDescription;
		this.addDate = addDate;
		this.deadline = deadline;
		this.deadlineTime = deadlineTime;
		this.isDone = IsDone.no;
		this.withDeadline = withDeadline;
	}
	
	////////////////// Constructors for finished tasks /////////////////////////////////
	
	public Task(int id, String taskName, String taskDescription, LocalDate addDate, LocalDate finishDate) {
		this.id = id;
		this.taskName = taskName;
		this.taskDescription = taskDescription;
		this.addDate = addDate;
		this.isDone = IsDone.yes;
		this.finishDate = finishDate;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	public Task newTask(String taskName, String taskDescription) {
		return new Task(taskName, taskDescription);
	}
	/////////////////////////////////////////////////////////////////////////////////////
	public void setAddDate(LocalDate addDate) {
		this.addDate = addDate;
	}
	
	public int getId() {
		return id;
	}
	
	public String getTaskName() {
		return taskName;
	}
	
	public String getTaskDescription() {
		return taskDescription;
	}
	
	public LocalDate getAddDate() {
		return addDate;
	}
	
	public IsDone getTaskStatus() {
		return isDone;
	}
	
	public LocalDate getFinishDate() {
		return finishDate;
	}
	
	public Deadline getDeadlineStatus() {
		return withDeadline;
	}
	
	public String getDeadline() {
		return deadline;
	}
	
	public String getDeadlineTime() {
		return deadlineTime;
	}
}
