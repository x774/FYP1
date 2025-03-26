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
@WebServlet(name = "VoteNowServlet", urlPatterns = {"/voteNow"})
public class VoteNowServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int activityId = Integer.parseInt(request.getParameter("activityId"));
        User user = (User) session.getAttribute("user");

        try (Connection conn = DatabaseService.getConnection()) {
            String query = "SELECT * FROM votingactivity WHERE votingActivityID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, activityId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String allowedDomains = rs.getString("allowedEmailDomains");
                String status = rs.getString("status");
                String publicKey = rs.getString("publicKey");

                if (!"open".equalsIgnoreCase(status)) {
                    request.setAttribute("error", "This voting activity is closed.");
                    request.getRequestDispatcher("close.jsp").forward(request, response);
                    return;
                }

                // Verify the use email or domain
                String userEmail = user.getEmail();
                if (!allowedDomains.equals(userEmail)) {
                    String[] userDomainArray = userEmail.split("@");
                    if (userDomainArray.length > 1) {
                        String userDomain = userDomainArray[1];
                        if (!allowedDomains.contains(userDomain)) {
                            session.setAttribute("error", "You are not allowed to vote in this activity. [" + allowedDomains + "]");
                            response.sendRedirect("participant-votes");
                            return;
                        }
                    } else {
                        session.setAttribute("error", "You are not allowed to vote in this activity. Invalid email.");
                        response.sendRedirect("participant-votes");
                        return;
                    }
                }

                // to voting page
                request.setAttribute("activityId", activityId);
                request.setAttribute("publicKey", publicKey);
                request.getRequestDispatcher("pages/voting.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Voting activity not found.");
                request.getRequestDispatcher("participant-votes").forward(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Error validating voting activity", e);
        }
    }
}
