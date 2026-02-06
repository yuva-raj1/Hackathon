package main.java.com.notify.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import main.java.com.notify.model.Task;

public class TaskDAO {
    
    public Task createTask(Task task) throws SQLException {
        String sql = """
            INSERT INTO tasks (user_id, title, description, category, task_date, task_time, priority)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, task.getUserId());
            stmt.setString(2, task.getTitle());
            stmt.setString(3, task.getDescription());
            stmt.setString(4, task.getCategory());
            stmt.setString(5, task.getTaskDate());
            stmt.setString(6, task.getTaskTime());
            stmt.setString(7, task.getPriority());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                task.setId(rs.getInt(1));
            }
            return task;
        }
    }
    
    public List<Task> getTasksByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE user_id = ? ORDER BY task_date, task_time";
        List<Task> tasks = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tasks.add(extractTask(rs));
            }
        }
        return tasks;
    }
    
    public List<Task> getPendingTasks(int userId) throws SQLException {
        String sql = """
            SELECT * FROM tasks 
            WHERE user_id = ? AND completed = FALSE 
            ORDER BY task_date, task_time
        """;
        List<Task> tasks = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tasks.add(extractTask(rs));
            }
        }
        return tasks;
    }
    
    public List<Task> getTasksForReminder(int userId) throws SQLException {
        String sql = """
            SELECT * FROM tasks 
            WHERE user_id = ? AND completed = FALSE AND notified = FALSE
            AND CONCAT(task_date, ' ', task_time) <= DATEADD('MINUTE', 5, CURRENT_TIMESTAMP)
            ORDER BY task_date, task_time
        """;
        List<Task> tasks = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tasks.add(extractTask(rs));
            }
        }
        return tasks;
    }
    
    public boolean updateTask(Task task) throws SQLException {
        String sql = """
            UPDATE tasks 
            SET title = ?, description = ?, category = ?, task_date = ?, 
                task_time = ?, priority = ?, completed = ?
            WHERE id = ? AND user_id = ?
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getCategory());
            stmt.setString(4, task.getTaskDate());
            stmt.setString(5, task.getTaskTime());
            stmt.setString(6, task.getPriority());
            stmt.setBoolean(7, task.isCompleted());
            stmt.setInt(8, task.getId());
            stmt.setInt(9, task.getUserId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean markCompleted(int taskId, int userId) throws SQLException {
        String sql = "UPDATE tasks SET completed = TRUE, notified = TRUE WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, taskId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean markNotified(int taskId) throws SQLException {
        String sql = "UPDATE tasks SET notified = TRUE WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, taskId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteTask(int taskId, int userId) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, taskId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    private Task extractTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setUserId(rs.getInt("user_id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setCategory(rs.getString("category"));
        task.setTaskDate(rs.getString("task_date"));
        task.setTaskTime(rs.getString("task_time"));
        task.setPriority(rs.getString("priority"));
        task.setCompleted(rs.getBoolean("completed"));
        task.setNotified(rs.getBoolean("notified"));
        task.setCreatedAt(rs.getString("created_at"));
        return task;
    }
}

