/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import dao.CandidateDAO;
import dao.VoteDAO;
import dao.VotingActivityDAO;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
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
import models.Candidate;
import models.Vote;
import models.VotingActivity;
import services.DatabaseService;
import services.PaillierService;
import services.PaillierService.*;

/**
 *
 * @author joonx
 */
@WebServlet(name = "ViewResultServlet", urlPatterns = {"/viewResult"})
public class ViewResultServlet extends HttpServlet {

    private VoteDAO voteDAO;
    private VotingActivityDAO votingActivityDAO;
    private PaillierService paillierService;
    private CandidateDAO candidateDAO;

    @Override
    public void init() throws ServletException {
        try {
            System.out.println("Initializing servlet...");
            Connection conn = DatabaseService.getConnection();
            voteDAO = new VoteDAO(conn);
            votingActivityDAO = new VotingActivityDAO(conn);
            paillierService = new PaillierService();
            candidateDAO = new CandidateDAO();
            System.out.println("Initialization successful.");
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            throw new ServletException("Database connection error", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String activityIDParam = request.getParameter("activityID");
            System.out.println("Received activityID: " + activityIDParam);
            int activityID = Integer.parseInt(activityIDParam);

            // Get voting activity info
            VotingActivity activity = votingActivityDAO.getVotingActivityByID(activityID);
            if (activity == null) {
                System.err.println("Voting activity not found for activityID: " + activityID);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Voting activity not found.");
                return;
            }
            System.out.println("Found voting activity: " + activity.getVotingActivityName());

            // Get voted info
            List<Vote> votes = voteDAO.getVotesByActivityID(activityID);
            System.out.println("Fetched " + votes.size() + " votes for activityID: " + activityID);

            // Get paillier public key create encrypt context
            PaillierPublicKey paillierPublicKey = PaillierPublicKey.fromString(activity.getPaillierPublicKey());
            PaillierContext context = paillierPublicKey.createContext();
            System.out.println("Paillier public key loaded and context created.");

            // Get Paillier private key from the form input
            String privateKeyString = request.getParameter("privateKey");
            PaillierPrivateKey paillierPrivateKey = null;
            try {
                paillierPrivateKey = PaillierPrivateKey.fromString(privateKeyString, paillierPublicKey);
                System.out.println("Paillier private key loaded.");
            } catch (Exception e) {
                System.err.println("Invalid Paillier private key entered: " + e.getMessage());
                response.getWriter().write("<script>alert('Invalid private key'); window.history.back();</script>");
                return;
            }

            // Validate if the provided private key matches the public key for this activity
            PaillierKeyPair keyPair = new PaillierKeyPair(paillierPublicKey, paillierPrivateKey);
            if (!paillierService.verifyKeyPair(keyPair)) {
                System.out.println("Private key does not match the public key.");
                response.getWriter().write("<script>alert('Private key does not match the public key.'); window.history.back();</script>");
                return;
            }

            // Initialize a map to store the encrypted votes for each candidate
            Map<Integer, EncryptedNumber> candidateVotes = new HashMap<>();

            // Get all candidates (including those who might not have received any votes)
            List<Candidate> allCandidates = candidateDAO.getCandidatesByActivityId(activityID);
            Map<Integer, String> candidateNames = new HashMap<>();
            Map<Integer, Integer> candidateVoteCounts = new HashMap<>();

            System.out.println("Mapping candidates...");
            for (Candidate candidate : allCandidates) {
                candidateNames.put(candidate.getCandidateID(), candidate.getCandidateName());
                // Initialize all candidates' vote count to 0
                candidateVoteCounts.put(candidate.getCandidateID(), 0);
                System.out.println("Mapped candidateID: " + candidate.getCandidateID() + " to name: " + candidate.getCandidateName());
            }

            // Cout vote obaint for each canditade
            System.out.println("Starting vote aggregation...");
            for (Vote vote : votes) {
                EncryptedNumber encryptedVote = EncryptedNumber.deserialize(context, vote.getEncryptedVote());
                candidateVotes.compute(vote.getCandidateID(), (id, existingVote) -> {
                    if (existingVote == null) {
                        return encryptedVote;  // first vote for this candidate
                    } else {
                        return existingVote.add(encryptedVote);  // add encrypted votes for the candidate
                    }
                });
            }

            // Decrypt the final result
            System.out.println("Decrypting and calculating vote counts...");
            for (Map.Entry<Integer, EncryptedNumber> entry : candidateVotes.entrySet()) {
                BigInteger decryptedVoteCount = paillierPrivateKey.decrypt(entry.getValue());
                candidateVoteCounts.put(entry.getKey(), decryptedVoteCount.intValue());
            }
            System.out.println("Vote counts decryption complete.");

            // Make sure all candidates are included even if they have 0 votes
            for (Candidate candidate : allCandidates) {
                // If a candidate is missing in the vote counts map, set their votes to 0
                candidateVoteCounts.putIfAbsent(candidate.getCandidateID(), 0);
            }

            // count the %
            int totalVotes = candidateVoteCounts.values().stream().mapToInt(Integer::intValue).sum();
            System.out.println("Total votes: " + totalVotes);
            Map<Integer, Double> candidatePercentages = new HashMap<>();
            for (Map.Entry<Integer, Integer> entry : candidateVoteCounts.entrySet()) {
                double percentage = (double) entry.getValue() / totalVotes * 100;
                candidatePercentages.put(entry.getKey(), percentage);
                System.out.println("Candidate " + entry.getKey() + " has " + percentage + "% of the total votes.");
            }

            // find the winner top vote
            int maxVotes = candidateVoteCounts.values().stream().max(Integer::compare).orElse(0);
            List<Integer> topCandidateIDs = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : candidateVoteCounts.entrySet()) {
                if (entry.getValue() == maxVotes) {
                    topCandidateIDs.add(entry.getKey());
                }
            }
            System.out.println("Top candidate(s) with the most votes: " + topCandidateIDs);

            // save the winner date
            Map<Integer, String> topCandidates = new HashMap<>();
            for (Integer candidateID : topCandidateIDs) {
                String candidateName = candidateNames.get(candidateID);
                if (candidateName != null) {
                    topCandidates.put(candidateID, candidateName);
                }
            }
            
            // Handle candidates with no votes (already initialized to 0)
            for (Integer candidateID : candidateVoteCounts.keySet()) {
                if (!candidateNames.containsKey(candidateID)) {
                    System.out.println("Warning: No candidate name found for candidateID: " + candidateID);
                    candidateNames.put(candidateID, "Unknown");
                }
            }

            // save all data to session 
            HttpSession session = request.getSession();
            session.setAttribute("candidateVotes", candidateVoteCounts);
            session.setAttribute("candidatePercentages", candidatePercentages);
            session.setAttribute("candidateNames", candidateNames);
            session.setAttribute("totalVotes", totalVotes);
            session.setAttribute("activityName", activity.getVotingActivityName());
            session.setAttribute("topCandidates", topCandidates);
            session.setAttribute("maxVotes", maxVotes);
            session.setAttribute("activityID", activityID);
            System.out.println("Results set as request attributes.");

            response.sendRedirect("pages/viewResult.jsp");
            System.out.println("Forwarded to viewResult.jsp.");

        } catch (NumberFormatException e) {
            System.err.println("Invalid activity ID format: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid activity ID.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
            e.printStackTrace();
        }
    }
}
