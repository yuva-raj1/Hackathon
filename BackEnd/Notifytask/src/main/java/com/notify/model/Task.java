package main.java.com.notify.model;

public class Task {
    private int id;
    private int userId;
    private String title;
    private String description;
    private String category; // Study, Work, Travel, Market, Daily
    private String taskDate;
    private String taskTime;
    private String priority; // High, Medium, Low
    private boolean completed;
    private boolean notified;
    private String createdAt;
    
    public Task() {}
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getTaskDate() { return taskDate; }
    public void setTaskDate(String taskDate) { this.taskDate = taskDate; }
    
    public String getTaskTime() { return taskTime; }
    public void setTaskTime(String taskTime) { this.taskTime = taskTime; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    
    public boolean isNotified() { return notified; }
    public void setNotified(boolean notified) { this.notified = notified; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
