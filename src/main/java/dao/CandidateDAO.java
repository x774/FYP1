/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Candidate;
import services.DatabaseService;

/**
 *
 * @author joonx
 */
public class CandidateDAO {

    public void addCandidate(Candidate candidate) throws SQLException {
        String sql = "INSERT INTO candidates (votingActivityID, candidateName) VALUES (?, ?)";
        try (Connection conn = DatabaseService.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, candidate.getVotingActivityID());
            stmt.setString(2, candidate.getCandidateName());
            stmt.executeUpdate();
        }
    }

    public List<Candidate> getCandidatesByActivityId(int activityId) throws SQLException {
        String sql = "SELECT * FROM candidates WHERE votingActivityID = ?";
        List<Candidate> candidates = new ArrayList<>();
        try (Connection conn = DatabaseService.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, activityId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Candidate candidate = new Candidate();
                candidate.setCandidateID(rs.getInt("candidateID"));
                candidate.setCandidateName(rs.getString("candidateName"));
                candidate.setVotingActivityID(rs.getInt("votingActivityID"));
                candidates.add(candidate);
            }
        }
        return candidates;
    }

    public void deleteCandidate(int candidateID) throws SQLException {
        String sql = "DELETE FROM candidates WHERE candidateID = ?";
        try (Connection conn = DatabaseService.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, candidateID);
            stmt.executeUpdate();
        }
    }

    public void updateCandidate(Candidate candidate) throws SQLException {
        String sql = "UPDATE candidates SET candidateName = ? WHERE candidateID = ?";
        try (Connection conn = DatabaseService.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, candidate.getCandidateName());
            stmt.setInt(2, candidate.getCandidateID());
            stmt.executeUpdate();
        }
    }
}
