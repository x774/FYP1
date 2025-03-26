/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.User;
import models.VotingActivity;
import services.DatabaseService;

/**
 *
 * @author joonx
 */
@WebServlet(name = "OrganizerActivitiesServlet", urlPatterns = {"/organizer-activities"})
public class OrganizerActivitiesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("pages/login.jsp");
            return;
        }

        try (Connection conn = DatabaseService.getConnection()) {
            String query = "SELECT * " +
                           "FROM votingactivity WHERE userID = ? ORDER BY startTime DESC";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, user.getUserID());
                System.out.println("Executing query for userID: " + user.getUserID());
                
                ResultSet rs = ps.executeQuery();

                List<VotingActivity> activities = new ArrayList<>();
                while (rs.next()) {
                    VotingActivity activity = new VotingActivity();
                    activity.setVotingActivityID(rs.getInt("votingActivityID"));
                    activity.setVotingActivityName(rs.getString("votingActivityName"));
                    activity.setStartTime(rs.getDate("startTime"));
                    activity.setEndTime(rs.getDate("endTime"));
                    activity.setStatus(rs.getString("status"));
                    activity.setPublicKey(rs.getString("publicKey"));
                    
                    // Log each activity being processed
                    System.out.println("Processing activity: " + activity.getVotingActivityName() +
                            " (ID: " + activity.getVotingActivityID() + ")");

                    activities.add(activity);
                }
                
                request.setAttribute("activities", activities);
                request.getRequestDispatcher("pages/organizerActivities.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}