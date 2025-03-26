/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import dao.VoteDAO;
import dao.VotingActivityDAO;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import models.User;
import models.Vote;
import models.VotingActivity;
import services.DatabaseService;
import services.PaillierService;

/**
 *
 * @author joonx & KOH HUI QING
 */
@WebServlet(name = "SubmitVoteServlet", urlPatterns = {"/submitVote"})
public class SubmitVoteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = DatabaseService.getConnection()) {

            // Check user session
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                System.out.println("User session is null");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User session expired");
                return;
            }
            System.out.println("User authenticated: " + user.getEmail());

            // Get and validate activityId
            String activityIdParam = request.getParameter("activityId");
            if (activityIdParam == null || activityIdParam.isEmpty()) {
                System.out.println("Activity ID is missing");
                throw new IllegalArgumentException("activityId is missing.");
            }
            int activityId = Integer.parseInt(activityIdParam);
            System.out.println("Activity ID: " + activityId);

            // Get the candidate ID that the user selected
            String candidateIdParam = request.getParameter("candidateId");
            if (candidateIdParam == null || candidateIdParam.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Candidate ID is required.");
                return;
            }
            int candidateId = Integer.parseInt(candidateIdParam);  // Parse candidate ID
            System.out.println("Candidate ID: " + candidateId);

            // Get the RSA-encrypted vote from the client
            String encryptedVote = request.getParameter("encryptedVote");
            if (encryptedVote == null || encryptedVote.trim().isEmpty()) {
                System.out.println("Encrypted vote is null or empty");
                throw new Exception("No vote data provided.");
            }
            System.out.println("Encrypted vote length: " + encryptedVote.getBytes().length);

            System.out.println("Received encrypted vote data");

            /*         // RSA Decryption
            String candidateId = null;
            try {
                System.out.println("Attempting to decrypt vote...");
                candidateId = RSAUtil.decrypt(encryptedVote);
                System.out.println("Successfully decrypted");
            } catch (Exception e) {
                System.err.println("RSA decryption failed with error: " + e.getMessage());
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "RSA Decryption failed: " + e.getMessage());
                return;
            }

            // Validate the decrypted candidateId
            if (candidateId == null || candidateId.trim().isEmpty()) {
                System.out.println("Decrypted candidateId is null or empty");
                throw new Exception("Invalid decrypted data: candidateId is null or empty.");
            }

            // Validate that the decrypted candidateId is a valid number
            try {
                int candidateIdNum = Integer.parseInt(candidateId.trim());
                System.out.println("Valid candidate ID");
            } catch (NumberFormatException e) {
                System.err.println("Invalid candidate ID format: " + candidateId);
                throw new Exception("Invalid candidate ID format: must be a number");
            }
             */
            // Get Paillier public key
            VotingActivityDAO votingActivityDAO = new VotingActivityDAO(conn);
            VotingActivity activity = votingActivityDAO.getVotingActivityByID(activityId);

            if (activity == null || activity.getPaillierPublicKey() == null) {
                System.out.println("No voting activity or public key found for ID: " + activityId);
                throw new Exception("No public key available for the voting activity.");
            }
            System.out.println("Retrieved Paillier public key");

            // Paillier encryption
            PaillierService.PaillierPublicKey publicKey
                    = PaillierService.PaillierPublicKey.fromString(activity.getPaillierPublicKey());
            PaillierService.PaillierContext context = publicKey.createContext();
            System.out.println("Paillier public key and context successfully initialized.");

            try {
                BigInteger voteData = BigInteger.ONE; // Fix the vote increase with 1
                System.out.println("Encrypting vote data: " + voteData);
                PaillierService.EncryptedNumber encryptedVoteData = context.encrypt(voteData);
                //BigInteger encryptedVoteData = new BigInteger(encryptedVote);
                //PaillierService.EncryptedNumber encryptedPaillierVote = context.encrypt(encryptedVoteData);
                System.out.println("Successfully encrypted vote with Paillier");
                System.out.println("Encrypted vote data: " + encryptedVoteData);

                // Save vote
                Vote vote = new Vote();
                vote.setActivityID(activityId);
                vote.setUserID(user.getUserID());
                vote.setEncryptedVote(encryptedVoteData.serialize());
                vote.setCandidateID(candidateId);
                System.out.println("Vote object created. Saving vote to database...");

                VoteDAO voteDAO = new VoteDAO(conn);
                voteDAO.saveVote(vote);
                System.out.println("Successfully saved vote to database");

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Vote submitted successfully");
            } catch (NumberFormatException e) {
                //       System.out.println("Error parsing candidate ID: " + candidateId);
                throw new Exception("Invalid candidate ID format");
            }

        } catch (Exception e) {
            System.out.println("Error in vote submission: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Vote submission failed: " + e.getMessage());
        }
    }
}
