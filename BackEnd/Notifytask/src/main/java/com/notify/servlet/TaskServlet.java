package main.java.com.notify.servlet;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import main.java.com.notify.dao.TaskDAO;
import main.java.com.notify.model.Task;

@WebServlet("/api/tasks/*")
public class TaskServlet extends HttpServlet {
    
    private TaskDAO taskDAO = new TaskDAO();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);
        
        if (session == null || session.getAttribute("userId") == null) {
            resp.setStatus(401);
            out.print("{\"success\":false,\"message\":\"Not authenticated\"}");
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        String pathInfo = req.getPathInfo();
        
        try {
            List<Task> tasks;
            
            if ("/pending".equals(pathInfo)) {
                tasks = taskDAO.getPendingTasks(userId);
            } else {
                tasks = taskDAO.getTasksByUser(userId);
            }
            
            JSONArray jsonArray = new JSONArray();
            for (Task task : tasks) {
                JSONObject obj = new JSONObject();
                obj.put("id", task.getId());
                obj.put("title", task.getTitle());
                obj.put("description", task.getDescription());
                obj.put("category", task.getCategory());
                obj.put("taskDate", task.getTaskDate());
                obj.put("taskTime", task.getTaskTime());
                obj.put("priority", task.getPriority());
                obj.put("completed", task.isCompleted());
                obj.put("notified", task.isNotified());
                jsonArray.put(obj);
            }
            
            JSONObject response = new JSONObject();
            response.put("success", true);
            response.put("tasks", jsonArray);
            out.print(response);
            
        } catch (SQLException e) {
            resp.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);
        
        if (session == null || session.getAttribute("userId") == null) {
            resp.setStatus(401);
            out.print("{\"success\":false,\"message\":\"Not authenticated\"}");
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        
        try {
            Task task = new Task();
            task.setUserId(userId);
            task.setTitle(req.getParameter("title"));
            task.setDescription(req.getParameter("description"));
            task.setCategory(req.getParameter("category"));
            task.setTaskDate(req.getParameter("taskDate"));
            task.setTaskTime(req.getParameter("taskTime"));
            task.setPriority(req.getParameter("priority"));
            
            Task created = taskDAO.createTask(task);
            
            JSONObject json = new JSONObject();
            json.put("success", true);
            json.put("message", "Task created successfully");
            json.put("taskId", created.getId());
            out.print(json);
            
        } catch (SQLException e) {
            resp.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);
        
        if (session == null || session.getAttribute("userId") == null) {
            resp.setStatus(401);
            out.print("{\"success\":false,\"message\":\"Not authenticated\"}");
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            JSONObject jsonInput = new JSONObject(sb.toString());
            int taskId = jsonInput.getInt("id");
            
            // Handle complete action
            if (jsonInput.has("completed") && jsonInput.getBoolean("completed")) {
                boolean success = taskDAO.markCompleted(taskId, userId);
                JSONObject json = new JSONObject();
                json.put("success", success);
                json.put("message", success ? "Task marked as completed" : "Task not found");
                out.print(json);
                return;
            }
            
            // Handle full update
            Task task = new Task();
            task.setId(taskId);
            task.setUserId(userId);
            task.setTitle(jsonInput.getString("title"));
            task.setDescription(jsonInput.getString("description"));
            task.setCategory(jsonInput.getString("category"));
            task.setTaskDate(jsonInput.getString("taskDate"));
            task.setTaskTime(jsonInput.getString("taskTime"));
            task.setPriority(jsonInput.getString("priority"));
            
            boolean success = taskDAO.updateTask(task);
            
            JSONObject json = new JSONObject();
            json.put("success", success);
            json.put("message", success ? "Task updated successfully" : "Task not found");
            out.print(json);
            
        } catch (SQLException e) {
            resp.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);
        
        if (session == null || session.getAttribute("userId") == null) {
            resp.setStatus(401);
            out.print("{\"success\":false,\"message\":\"Not authenticated\"}");
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        String pathInfo = req.getPathInfo();
        
        try {
            int taskId = Integer.parseInt(pathInfo.substring(1));
            boolean success = taskDAO.deleteTask(taskId, userId);
            
            JSONObject json = new JSONObject();
            json.put("success", success);
            json.put("message", success ? "Task deleted successfully" : "Task not found");
            out.print(json);
            
        } catch (Exception e) {
            resp.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}
