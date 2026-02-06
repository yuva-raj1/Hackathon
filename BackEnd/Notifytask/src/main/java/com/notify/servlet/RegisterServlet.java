package main.java.com.notify.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import main.java.com.notify.dao.UserDAO;
import main.java.com.notify.model.User;

@WebServlet("/api/register")
public class RegisterServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAO();
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        JSONObject json = new JSONObject();
        
        try {
            String username = req.getParameter("username");
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            
            // Validation
            if (username == null || username.trim().isEmpty() || 
                password == null || password.length() < 4) {
                resp.setStatus(400);
                json.put("success", false);
                json.put("message", "Invalid input data");
                out.print(json);
                return;
            }
            
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            
            User created = userDAO.register(user);
            
            json.put("success", true);
            json.put("message", "Registration successful");
            json.put("userId", created.getId());
            
        } catch (SQLException e) {
            resp.setStatus(500);
            json.put("success", false);
            if (e.getMessage().contains("Unique index")) {
                json.put("message", "Username or email already exists");
            } else {
                json.put("message", "Database error: " + e.getMessage());
            }
        }
        
        out.print(json);
    }
}
