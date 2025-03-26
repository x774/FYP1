/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import dao.ActivityResultDAO;
import dao.CandidateDAO;
import dao.VoteDAO;
import dao.VotingActivityDAO;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
import models.ActivityResult;
import models.Candidate;
import models.Vote;
import models.VotingActivity;
import org.json.JSONObject;
import services.DatabaseService;
import services.PaillierService;
import services.PaillierService.EncryptedNumber;
import services.PaillierService.PaillierContext;
import services.PaillierService.PaillierPrivateKey;
import services.PaillierService.PaillierPublicKey;

/**
 *
 * @author joonx
 */
@WebServlet(name = "CloseVoteServlet", urlPatterns = {"/closeVote"})
public class CloseVoteServlet extends HttpServlet {

    private VoteDAO voteDAO;
    private VotingActivityDAO votingActivityDAO;
    private CandidateDAO candidateDAO;
    private ActivityResultDAO activityResultDAO;
    private PaillierService paillierService;

    @Override
    public void init() throws ServletException {
        try {
            Connection conn = DatabaseService.getConnection();
            voteDAO = new VoteDAO(conn);
            votingActivityDAO = new VotingActivityDAO(conn);
            candidateDAO = new CandidateDAO();
            activityResultDAO = new ActivityResultDAO(conn);
            paillierService = new PaillierService();
        } catch (SQLException e) {
            throw new ServletException("Database connection error", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Get activityID
            String activityIDParam = request.getParameter("activityID");
            System.out.println("Received activityID: " + activityIDParam);
            int activityID = Integer.parseInt(activityIDParam);

            VotingActivity activity = votingActivityDAO.getVotingActivityByID(activityID);
            if (activity == null) {
                response.getWriter().write("<script>alert('Activity not found.'); window.history.back();</script>");
                return;
            }

            List<Vote> votes = voteDAO.getVotesByActivityID(activityID);
            if (votes.isEmpty()) {
                response.getWriter().write("<script>alert('No votes found for this activity.'); window.history.back();</script>");
                return;
            }

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
            PaillierService.PaillierKeyPair keyPair = new PaillierService.PaillierKeyPair(paillierPublicKey, paillierPrivateKey);
            if (!paillierService.verifyKeyPair(keyPair)) {
                System.out.println("Private key does not match the public key.");
                response.getWriter().write("<script>alert('Private key does not match the public key.'); window.history.back();</script>");
                return;
            }
            System.out.println("Private key validation successful.");

            // Initialize a map to store the encrypted votes for each candidate
            Map<Integer, EncryptedNumber> candidateVotes = new HashMap<>();

            // Get all candidates (including those who might not have received any votes)
            List<Candidate> allCandidates = candidateDAO.getCandidatesByActivityId(activityID);
            Map<Integer, String> candidateNames = new HashMap<>();
            Map<Integer, Integer> candidateVoteCounts = new HashMap<>();

            // init candidate name and vote
            for (Candidate candidate : allCandidates) {
                candidateNames.put(candidate.getCandidateID(), candidate.getCandidateName());
                // Initialize all candidates' vote count to 0
                candidateVoteCounts.put(candidate.getCandidateID(), 0);
                System.out.println("Mapped candidateID: " + candidate.getCandidateID() + " to name: " + candidate.getCandidateName());
            }

            // homomorphic addition
            long startHomomorphicTime = System.nanoTime();
            System.out.println("Homomorphic Paillier Addition");
            System.out.println("Starting vote aggregation...");
            for (Vote vote : votes) {
                EncryptedNumber encryptedVote = EncryptedNumber.deserialize(context, vote.getEncryptedVote());
                System.out.println("Encrypted vote for candidate " + vote.getCandidateID() + ": " + encryptedVote);

                candidateVotes.compute(vote.getCandidateID(), (id, existingVote) -> {
                    if (existingVote == null) {
                        return encryptedVote;  // first time
                    } else {
                        return existingVote.add(encryptedVote);  // add encrypt vote
                    }
                });
            }
            System.out.println("Vote aggregation complete.");

            // Decrypt results
            System.out.println("Decrypting and calculating vote counts...");
            for (Map.Entry<Integer, EncryptedNumber> entry : candidateVotes.entrySet()) {
                BigInteger decryptedVoteCount = paillierPrivateKey.decrypt(entry.getValue());
                System.out.println("Decrypted vote count for candidate " + entry.getKey() + ": " + decryptedVoteCount);
                candidateVoteCounts.put(entry.getKey(), decryptedVoteCount.intValue());
            }
            System.out.println("Vote counts decryption complete.");

            long endHomomorphicTime = System.nanoTime();
            long homomorphicDuration = endHomomorphicTime - startHomomorphicTime;
            System.out.println("Homomorphic addition duration: " + homomorphicDuration + " ns");

            Map<Integer, Integer> individualVoteCounts = new HashMap<>();
            // int all candidate vote to 0
            for (Candidate candidate : allCandidates) {
                individualVoteCounts.put(candidate.getCandidateID(), 0);
            }

            // Individual Decryption Method
            long startDecryptionTime = System.nanoTime();
            System.out.println("Individual Decryption Method");
            System.out.println("Starting individual decryption of votes...");
            for (Vote vote : votes) {
                System.out.println("Processing vote for candidateID: " + vote.getCandidateID() + " (Encrypted vote data: " + vote.getEncryptedVote() + ")");

                // Deserialize the encrypted vote
                EncryptedNumber encryptedVote = EncryptedNumber.deserialize(context, vote.getEncryptedVote());
                System.out.println("Deserialized encrypted vote for candidateID " + vote.getCandidateID() + ": " + encryptedVote);

                // Decrypt the encrypted vote using the Paillier private key
                BigInteger decryptedVote = paillierPrivateKey.decrypt(encryptedVote);
                System.out.println("Decrypted vote for candidate " + vote.getCandidateID() + ": " + decryptedVote);

                // Update the individual vote counts
                individualVoteCounts.put(vote.getCandidateID(), individualVoteCounts.getOrDefault(vote.getCandidateID(), 0) + decryptedVote.intValue());
                System.out.println("Updated vote count for candidateID " + vote.getCandidateID() + ": " + individualVoteCounts.get(vote.getCandidateID()));
            }
            System.out.println("Vote counts decryption complete.");

            long endDecryptionTime = System.nanoTime();
            long decryptionDuration = endDecryptionTime - startDecryptionTime;
            System.out.println("Individual decryption duration: " + decryptionDuration + " ns");

            // verify
            System.out.println("Verifying homomorphic and individual decryption results...");
            System.out.println("Homomorphic results: " + candidateVoteCounts);
            System.out.println("Individual decryption results: " + individualVoteCounts);

            boolean resultsMatch = candidateVoteCounts.equals(individualVoteCounts);
            System.out.println("Do homomorphic and individual results match? " + resultsMatch);

            // Calculate total votes, end couting time and percentages
            int totalVotes = candidateVoteCounts.values().stream().mapToInt(Integer::intValue).sum();
            System.out.println("Total votes: " + totalVotes);

            // Calculate vote percentages
            Map<Integer, Double> candidatePercentages = new HashMap<>();
            for (Map.Entry<Integer, Integer> entry : candidateVoteCounts.entrySet()) {
                double percentage = (double) entry.getValue() / totalVotes * 100;
                candidatePercentages.put(entry.getKey(), percentage);
                System.out.println("Candidate " + entry.getKey() + ": " + entry.getValue() + " votes (" + percentage + "%)");
            }

            // Find winner(s)
            int maxVotes = candidateVoteCounts.values().stream().max(Integer::compare).orElse(0);
            List<Integer> topCandidateIDs = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : candidateVoteCounts.entrySet()) {
                if (entry.getValue() == maxVotes) {
                    topCandidateIDs.add(entry.getKey());
                }
            }
            System.out.println("Top candidate(s) ID(s): " + topCandidateIDs);

            String winnerName = topCandidateIDs.size() == 1
                    ? candidateNames.get(topCandidateIDs.get(0))
                    : null;
            System.out.println("Winner name: " + winnerName);

            // Prepare result summary
            System.out.println("Preparing result summary...");
            JSONObject resultSummary = new JSONObject();
            for (Map.Entry<Integer, Integer> entry : candidateVoteCounts.entrySet()) {
                JSONObject candidateResult = new JSONObject();
                candidateResult.put("votes", entry.getValue());
                candidateResult.put("percentage", candidatePercentages.get(entry.getKey()));
                resultSummary.put(candidateNames.getOrDefault(entry.getKey(), "Unknown"), candidateResult);
                System.out.println("Candidate " + candidateNames.getOrDefault(entry.getKey(), "Unknown") + ": "
                        + entry.getValue() + " votes, " + candidatePercentages.get(entry.getKey()) + "%");
            }

            // Save result to database
            ActivityResult result = new ActivityResult(
                    activityID, totalVotes,
                    topCandidateIDs.size() == 1 ? topCandidateIDs.get(0) : null,
                    winnerName, resultSummary.toString()
            );

            boolean resultSaved = activityResultDAO.saveActivityResult(result, homomorphicDuration, decryptionDuration, resultsMatch);
            System.out.println("Result saved to database: " + resultSaved);

            // Update voting activity status
            System.out.println("Updating voting activity status...");
            LocalDateTime closeTime = LocalDateTime.now();
            java.sql.Date sqlCloseTime = java.sql.Date.valueOf(closeTime.toLocalDate());  // Convert to java.sql.Date (only date part)

            boolean updated = votingActivityDAO.updateVotingActivityStatusAndEndTime(activityID, "close", sqlCloseTime);
            if (updated) {
                System.out.println("Voting activity status updated to 'close' with endTime: " + closeTime);
            } else {
                System.err.println("Failed to update voting activity status.");
            }

            HttpSession session = request.getSession();
            session.setAttribute("activityID", activityID);
            response.sendRedirect("generateReport");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing the vote.");
            e.printStackTrace();
        }
    }
}
