/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import models.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import services.DatabaseService;

/**
 *
 * @author joonx
 */
public class VoteDAO {

    private Connection conn;

    public VoteDAO(Connection conn) {
        this.conn = conn;
    }

    public VoteDAO() {
    }

    public List<Vote> getVotesByActivityID(int activityID) throws SQLException {
        String query = "SELECT * FROM Vote WHERE activityID = ?";
        List<Vote> votes = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, activityID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Vote vote = new Vote();
                    vote.setVoteID(rs.getInt("voteID"));
                    vote.setActivityID(rs.getInt("activityID"));
                    vote.setCandidateID(rs.getInt("candidateID"));
                    vote.setEncryptedVote(rs.getString("encryptedVote")); // Retrieve from ResultSet
                    votes.add(vote);
                }
            }
        }
        return votes;
    }

    public void saveVote(Vote vote) {
        try (Connection conn = DatabaseService.getConnection()) {
            String sql = "INSERT INTO vote (userID, activityID, encryptedVote, candidateID) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, vote.getUserID());
            ps.setInt(2, vote.getActivityID());
            ps.setString(3, vote.getEncryptedVote());
            ps.setInt(4, vote.getCandidateID());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
