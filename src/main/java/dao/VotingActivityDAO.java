/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import models.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joonx
 */
public class VotingActivityDAO {

    private Connection conn;

    public VotingActivityDAO(Connection conn) {
        this.conn = conn;
    }

    public VotingActivityDAO() {
    }

    

    public VotingActivity getVotingActivityByID(int activityID) throws SQLException {
        String query = "SELECT * FROM VotingActivity WHERE votingActivityID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, activityID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    VotingActivity activity = new VotingActivity();
                    activity.setVotingActivityID(rs.getInt("votingActivityID"));
                    activity.setUserID(rs.getInt("userID"));
                    activity.setVotingActivityName(rs.getString("votingActivityName"));
                    activity.setAllowedEmailDomains(rs.getString("allowedEmailDomains"));
                    activity.setPublicKey(rs.getString("publicKey"));
                    activity.setPaillierPublicKey(rs.getString("paillierPublicKey"));
                    activity.setStatus(rs.getString("status"));
                    activity.setEndTime(rs.getDate("endTime"));
                    activity.setStartTime(rs.getDate("startTime"));
                    return activity;
                }
            }
        }
        return null;
    }

    
    public List<VotingActivity> getVotingActivitiesByUser(int userID) throws SQLException {
        String query = "SELECT * FROM VotingActivity WHERE userID = ?";
        List<VotingActivity> activities = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    VotingActivity activity = new VotingActivity();
                    activity.setVotingActivityID(rs.getInt("votingActivityID"));
                    activity.setUserID(rs.getInt("userID"));
                    activity.setVotingActivityName(rs.getString("votingActivityName"));
                    activity.setAllowedEmailDomains(rs.getString("allowedEmailDomains"));
                    activity.setPublicKey(rs.getString("publicKey"));
                    activities.add(activity);
                }
            }
        }
        return activities;
    }

    

    public boolean updateVotingActivityStatusAndEndTime(int activityID, String status, Date endTime) throws SQLException {
        String sql = "UPDATE VotingActivity SET status = ?, endTime = ? WHERE votingActivityID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setDate(2, endTime);
            stmt.setInt(3, activityID);

            return stmt.executeUpdate() > 0;
        }
    }

    public List<VotingActivity> getClosedActivities() throws SQLException {
        String query = "SELECT * FROM VotingActivity WHERE status = 'close'";
        List<VotingActivity> activities = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                VotingActivity activity = new VotingActivity();
                activity.setVotingActivityID(rs.getInt("votingActivityID"));
                activity.setVotingActivityName(rs.getString("votingActivityName"));
                activity.setStartTime(rs.getTimestamp("startTime"));
                activity.setEndTime(rs.getTimestamp("endTime"));
                activities.add(activity);
            }
        }
        return activities;
    }
}
