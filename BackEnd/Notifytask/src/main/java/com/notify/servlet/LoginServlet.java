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

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAO();
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        JSONObject json = new JSONObject();
        
        try {
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            
            User user = userDAO.login(username, password);
            
            if (user != null) {
                HttpSession session = req.getSession();
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                
                json.put("success", true);
                json.put("message", "Login successful");
                json.put("username", user.getUsername());
            } else {
                resp.setStatus(401);
                json.put("success", false);
                json.put("message", "Invalid username or password");
            }
            
        } catch (SQLException e) {
            resp.setStatus(500);
            json.put("success", false);
            json.put("message", "Database error: " + e.getMessage());
        }
        
        out.print(json);
    }
}
