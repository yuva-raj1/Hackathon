package main.java.com.notify.servlet;



import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/api/logout")
public class LogoutServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        JSONObject json = new JSONObject();
        json.put("success", true);
        json.put("message", "Logged out successfully");
        out.print(json);
    }
}
