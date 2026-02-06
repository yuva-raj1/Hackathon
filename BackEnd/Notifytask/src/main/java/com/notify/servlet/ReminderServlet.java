package main.java.com.notify.servlet;

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

@WebServlet("/api/reminders")
public class ReminderServlet extends HttpServlet {
    
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
        
        try {
            List<Task> reminders = taskDAO.getTasksForReminder(userId);
            
            JSONArray jsonArray = new JSONArray();
            for (Task task : reminders) {
                JSONObject obj = new JSONObject();
                obj.put("id", task.getId());
                obj.put("title", task.getTitle());
                obj.put("category", task.getCategory());
                obj.put("taskTime", task.getTaskTime());
                obj.put("priority", task.getPriority());
                jsonArray.put(obj);
                
                // Mark as notified so we don't remind again immediately
                taskDAO.markNotified(task.getId());
            }
            
            JSONObject response = new JSONObject();
            response.put("success", true);
            response.put("reminders", jsonArray);
            out.print(response);
            
        } catch (SQLException e) {
            resp.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}
