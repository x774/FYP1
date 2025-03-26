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
import models.Candidate;
import models.User;
import models.VotingActivity;
import services.DatabaseService;

/**
 *
 * @author joonx
 */
@WebServlet(name = "EditActivityServlet", urlPatterns = {"/edit-activity"})
public class EditActivityServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("pages/login.jsp");
            return;
        }

        String activityID = request.getParameter("activityID");

        try (Connection conn = DatabaseService.getConnection()) {
            // Get activity details
            String activityQuery = "SELECT * FROM votingactivity WHERE votingActivityID = ? AND userID = ?";
            try (PreparedStatement ps = conn.prepareStatement(activityQuery)) {
                ps.setInt(1, Integer.parseInt(activityID));
                ps.setInt(2, user.getUserID());

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    VotingActivity activity = new VotingActivity();
                    activity.setVotingActivityID(rs.getInt("votingActivityID"));
                    activity.setVotingActivityName(rs.getString("votingActivityName"));
                    activity.setVotingDescription(rs.getString("votingDescription"));
                    activity.setStartTime(rs.getDate("startTime"));
                    activity.setEndTime(rs.getDate("endTime"));
                    activity.setStatus(rs.getString("status"));
                    activity.setAllowedEmailDomains(rs.getString("allowedEmailDomains"));

                    System.out.println("Activity details fetched successfully. Activity name: " + activity.getVotingActivityName());

                    // Get candidates
                    String candidateQuery = "SELECT * FROM candidates WHERE votingActivityID = ?";
                    try (PreparedStatement psCandidate = conn.prepareStatement(candidateQuery)) {
                        psCandidate.setInt(1, Integer.parseInt(activityID));
                        ResultSet rsCandidate = psCandidate.executeQuery();

                        List<Candidate> candidates = new ArrayList<>();
                        while (rsCandidate.next()) {
                            Candidate candidate = new Candidate();
                            candidate.setCandidateID(rsCandidate.getInt("candidateID"));
                            candidate.setCandidateName(rsCandidate.getString("candidateName"));
                            candidates.add(candidate);
                        }

                        request.setAttribute("candidates", candidates);
                        System.out.println("Candidates fetched: " + candidates.size() + " candidates found.");
                    }

                    request.setAttribute("activity", activity);
                    request.getRequestDispatcher("pages/editViewActivity.jsp").forward(request, response);
                } else {
                    System.out.println("Activity not found or unauthorized access.");
                    response.sendRedirect("organizer-activities?message=Activity not found or unauthorized");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("organizer-activities");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("pages/login.jsp");
            return;
        }

        String activityID = request.getParameter("activityID");
        String name = request.getParameter("activity_name");
        String description = request.getParameter("activity_description");
        String allowedDomains = request.getParameter("allowed_domains");
        String[] candidateNames = request.getParameterValues("candidate_name[]");

        System.out.println("Processing activity edit for activityID: " + activityID);
        System.out.println("New name: " + name);
        System.out.println("New description: " + description);
        System.out.println("Allowed email domains: " + allowedDomains);
        System.out.println("Candidates to be updated: " + (candidateNames != null ? candidateNames.length : 0));

        try (Connection conn = DatabaseService.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Update activity details
                System.out.println("Updating activity details in the database...");
                String updateActivity = "UPDATE votingactivity SET votingActivityName = ?, "
                        + "votingDescription = ?, allowedEmailDomains = ? "
                        + "WHERE votingActivityID = ? AND userID = ?";
                try (PreparedStatement ps = conn.prepareStatement(updateActivity)) {
                    ps.setString(1, name);
                    ps.setString(2, description);
                    ps.setString(3, allowedDomains);
                    ps.setInt(4, Integer.parseInt(activityID));
                    ps.setInt(5, user.getUserID());
                    ps.executeUpdate();
                    System.out.println("Activity details updated successfully.");
                }

                // Get current candidates from the database
                String getCurrentCandidates = "SELECT candidateID, candidateName FROM candidates WHERE votingActivityID = ?";
                List<String> currentCandidateNames = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement(getCurrentCandidates)) {
                    ps.setInt(1, Integer.parseInt(activityID));
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            currentCandidateNames.add(rs.getString("candidateName"));
                        }
                    }
                }

                // Identify candidates to delete (those removed by the user)
                List<String> candidatesToDelete = new ArrayList<>(currentCandidateNames);
                if (candidateNames != null) {
                    // Remove existing candidates from the deletion list if they are still present
                    for (String candidateName : candidateNames) {
                        candidatesToDelete.remove(candidateName.trim());
                    }
                }

                // Delete removed candidates from the database
                if (!candidatesToDelete.isEmpty()) {
                    System.out.println("Deleting candidates: " + candidatesToDelete);
                    String deleteCandidates = "DELETE FROM candidates WHERE votingActivityID = ? AND candidateName = ?";
                    try (PreparedStatement ps = conn.prepareStatement(deleteCandidates)) {
                        for (String candidateName : candidatesToDelete) {
                            ps.setInt(1, Integer.parseInt(activityID));
                            ps.setString(2, candidateName);
                            ps.addBatch();
                        }
                        ps.executeBatch();
                        System.out.println("Deleted " + candidatesToDelete.size() + " candidates.");
                    }
                }

                // Insert new candidates (those not currently in the database)
                if (candidateNames != null && candidateNames.length > 0) {
                    System.out.println("Inserting new candidates...");
                    String insertCandidate = "INSERT INTO candidates (votingActivityID, candidateName) VALUES (?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insertCandidate)) {
                        for (String candidateName : candidateNames) {
                            if (candidateName != null && !candidateName.trim().isEmpty()
                                    && !currentCandidateNames.contains(candidateName.trim())) {
                                ps.setInt(1, Integer.parseInt(activityID));
                                ps.setString(2, candidateName.trim());
                                ps.addBatch();
                            }
                        }
                        ps.executeBatch();
                        System.out.println("Inserted new candidates.");
                    }
                }

                conn.commit();
                System.out.println("Transaction committed. Redirecting to success page.");
                // Redirect with success message
                response.sendRedirect("edit-activity?activityID=" + activityID + "&success=true");
            } catch (Exception e) {
                conn.rollback();
                System.err.println("Error during transaction. Rolling back. Error: " + e.getMessage());
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("edit-activity?activityID=" + activityID + "&error=true");
        }
    }
}
