/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.User;
import services.DatabaseService;
import services.PaillierService;

/**
 *
 * @author joonx
 */
@WebServlet(name = "SetupVotingServlet", urlPatterns = {"/setup-voting"})
public class SetupVotingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String activityName = request.getParameter("activity_name");
        String activityDescription = request.getParameter("activity_description");
        String startDate = request.getParameter("start_date");
        String endDate = request.getParameter("end_date");
        String allowedDomains = request.getParameter("allowed_domains");

        String[] candidateNames = request.getParameterValues("candidate_name[]");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("pages/login.jsp");
            return;
        }

        try (Connection conn = DatabaseService.getConnection()) {
            conn.setAutoCommit(false);

            // Generate RSA key
            System.out.println("Generating RSA key pair...");
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            System.out.println("Generated RSA public key: " + publicKey);
            System.out.println("Generated RSA private key: " + privateKey);

            // Generate Paillier keys
            System.out.println("Generating Paillier key pair...");
            PaillierService.PaillierKeyPair paillierKeyPair = PaillierService.generateKeyPair(2048);
            String paillierPublicKey = paillierKeyPair.getPublicKey().toString();
            String paillierPrivateKey = paillierKeyPair.getPrivateKey().toString();
            System.out.println("Generated Paillier public key: " + paillierPublicKey);
            System.out.println("Generated Paillier private key: " + paillierPrivateKey);

            // Insert VotingActivity
            String insertActivitySQL = "INSERT INTO votingactivity (userID, votingActivityName, votingDescription,"
                    + " allowedEmailDomains, publicKey, startTime, endTime, status, paillierPublicKey) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertActivitySQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, user.getUserID());
                ps.setString(2, activityName);
                ps.setString(3, activityDescription);
                ps.setString(4, allowedDomains);
                ps.setString(5, publicKey);
                ps.setString(6, startDate);
                ps.setString(7, endDate);
                ps.setString(8, "open");
                ps.setString(9, paillierPublicKey);
                System.out.println("Inserting new VotingActivity with name: " + activityName);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int activityID = rs.getInt(1);
                    System.out.println("Inserted VotingActivity with ID: " + activityID);

                    // Insert candidate
                    if (candidateNames != null && candidateNames.length > 0) {
                        String insertCandidateSQL = "INSERT INTO candidates (votingActivityID, candidateName) VALUES (?, ?)";
                        try (PreparedStatement psCandidate = conn.prepareStatement(insertCandidateSQL)) {
                            for (String candidate : candidateNames) {
                                psCandidate.setInt(1, activityID);
                                psCandidate.setString(2, candidate);
                                psCandidate.addBatch();
                            }
                            System.out.println("Inserting candidates: " + String.join(", ", candidateNames));
                            psCandidate.executeBatch();
                        }
                    } else {
                        System.out.println("No candidates provided.");
                        response.getWriter().write("<script>alert('No candidate inserted.'); window.history.back();</script>");
                        return;
                    }
                }
            }

            conn.commit();
            System.out.println("Transaction committed successfully.");

            session.setAttribute("publicKey", publicKey);
            session.setAttribute("privateKey", privateKey);
            session.setAttribute("paillierPublicKey", paillierPublicKey);
            session.setAttribute("paillierPrivateKey", paillierPrivateKey);
            session.setAttribute("activityName", activityName);

            response.sendRedirect("pages/setupSuccess.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("<script>alert('Something error.'); window.history.back();</script>");
        }
    }
}
