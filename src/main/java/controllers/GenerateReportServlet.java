/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import dao.ActivityResultDAO;
import dao.VotingActivityDAO;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.ActivityResult;
import models.ResultRow;
import models.User;
import models.VotingActivity;
import services.DatabaseService;

/**
 *
 * @author joonx
 */
@WebServlet(name = "GenerateReportServlet", urlPatterns = {"/generateReport"})
public class GenerateReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("pages/login.jsp");
            return;
        }

        // Fetch all closed activities
        try (Connection conn = DatabaseService.getConnection()) {
            VotingActivityDAO activityDAO = new VotingActivityDAO(conn);

            System.out.println("Fetching all closed activities...");
            List<VotingActivity> closedActivities = activityDAO.getClosedActivities();
            request.setAttribute("closedActivities", closedActivities);

            // Forward to generateReport.jsp
            System.out.println("Found " + closedActivities.size() + " closed activities. Forwarding to generateReport.jsp.");
            request.getRequestDispatcher("pages/generateReport.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String reportType = request.getParameter("report_type");
        int activityID = Integer.parseInt(request.getParameter("votingActivity"));

        System.out.println("Generating report for Activity ID: " + activityID + " with report type: " + reportType);

        try (Connection conn = DatabaseService.getConnection()) {
            VotingActivityDAO activityDAO = new VotingActivityDAO(conn);
            ActivityResultDAO resultDAO = new ActivityResultDAO(conn);

            // Fetch activity details
            VotingActivity activity = activityDAO.getVotingActivityByID(activityID);
            System.out.println("Fetched activity details for Activity ID: " + activityID);

            if ("summary".equalsIgnoreCase(reportType)) {
                // Fetch summary data
                System.out.println("Generating summary report...");
                ActivityResult result = resultDAO.getActivityResult(activityID);

                // Get the candidate results from the ActivityResult object
                List<ResultRow> candidateResultsList = result.getCandidateResults();

                // Set attributes for the summary report
                request.setAttribute("activityName", activity.getVotingActivityName());
                request.setAttribute("totalValidVotes", result.getTotalVotes());
                request.setAttribute("votingResults", candidateResultsList);
                request.setAttribute("startTime", activity.getStartTime());
                request.setAttribute("endTime", activity.getEndTime());

                // Forward to summary report JSP
                request.setAttribute("activity", activity);
                System.out.println("Forwarding to summaryReport.jsp with summary data.");
                request.getRequestDispatcher("pages/summaryReport.jsp").forward(request, response);

            } else if ("voting-process".equalsIgnoreCase(reportType)) {
                // Fetch calculation time data
                System.out.println("Generating voting calculation process report...");
                ActivityResult result = resultDAO.getActivityResult(activityID);

                // Set attributes for the calculation time report
                request.setAttribute("activityName", activity.getVotingActivityName());
                request.setAttribute("totalVotes", result.getTotalVotes());
                request.setAttribute("homomorphicTime", result.getHomomorphicTime());
                request.setAttribute("decryptionTime", result.getDecryptionTime());
                request.setAttribute("resultsMatch", result.isResultsMatch());

                // Forward to calculation time report JSP
                request.setAttribute("activity", activity);
                System.out.println("Forwarding to calculationTimeReport.jsp with calculation data.");
                request.getRequestDispatcher("pages/calculationTimeReport.jsp").forward(request, response);
            } else {
                // Handle invalid report type
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid report type specified.");
            }
        } catch (SQLException e) {
            // Handle SQL exceptions (e.g., database connection issues, query failures)
            log("Database error while generating report", e);
            throw new ServletException("Error generating report due to database issue.", e);

        } catch (Exception e) {
            // Catch any other unexpected exceptions
            log("Unexpected error occurred while generating report", e);
            throw new ServletException("Unexpected error occurred while generating report", e);
        }
    }
}
