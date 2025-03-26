/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.User;
import services.DatabaseService;

/**
 *
 * @author joonx
 */
@WebServlet(name = "ParticipantVotesServlet", urlPatterns = {"/participant-votes"})
public class ParticipantVotesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("pages/login.jsp");
            return;
        }
        
        try (Connection conn = DatabaseService.getConnection()) {
            String query = "SELECT votingActivityID, votingActivityName, votingDescription, startTime, endTime, status FROM votingactivity WHERE status = 'open'";
            System.out.println("Executing query to fetch open voting activities.");
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            List<Map<String, Object>> votingActivities = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("id", rs.getInt("votingActivityID"));
                activity.put("name", rs.getString("votingActivityName"));
                activity.put("description", rs.getString("votingDescription"));
                activity.put("startTime", rs.getDate("startTime"));
                activity.put("endTime", rs.getDate("endTime"));
                activity.put("status", rs.getString("status"));
                
                 // Log each activity being processed
                System.out.println("Activity found: " + activity.get("name") + " (ID: " + activity.get("id") + ")");
                votingActivities.add(activity);
            }

            request.setAttribute("votingActivities", votingActivities);
            request.getRequestDispatcher("pages/participantVotes.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Error loading voting activities", e);
        }
    }
}
