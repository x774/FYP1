/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.ActivityResult;
import models.ResultRow;

/**
 *
 * @author joonx
 */
public class ActivityResultDAO {

    private final Connection conn;

    public ActivityResultDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean saveActivityResult(ActivityResult result, long homomorphicTime, long decryptionTime, boolean resultsMatch) throws SQLException {
        String sql = "INSERT INTO activity_results (activityID, totalVotes, winnerCandidateID, winnerName, resultSummary, homomorphicTime, decryptionTime, resultsMatch) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, result.getActivityID());
            stmt.setInt(2, result.getTotalVotes());

            if (result.getWinnerCandidateID() == null) {
                stmt.setNull(3, Types.INTEGER);
                stmt.setNull(4, Types.VARCHAR);
            } else {
                stmt.setInt(3, result.getWinnerCandidateID());
                stmt.setString(4, result.getWinnerName());
            }

            stmt.setString(5, result.getResultSummary());

            stmt.setLong(6, homomorphicTime);
            stmt.setLong(7, decryptionTime);
            stmt.setBoolean(8, resultsMatch);

            return stmt.executeUpdate() > 0;
        }
    }

    public ActivityResult getActivityResult(int activityID) throws SQLException {
        String sql = "SELECT * FROM activity_results WHERE activityID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, activityID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ActivityResult result = new ActivityResult();
                    result.setActivityID(activityID);
                    result.setTotalVotes(rs.getInt("totalVotes"));
                    result.setWinnerCandidateID(rs.getObject("winnerCandidateID") != null ? rs.getInt("winnerCandidateID") : null);
                    result.setWinnerName(rs.getString("winnerName"));
                    result.setResultSummary(rs.getString("resultSummary"));
                    result.setHomomorphicTime(rs.getLong("homomorphicTime"));
                    result.setDecryptionTime(rs.getLong("decryptionTime"));
                    result.setResultsMatch(rs.getBoolean("resultsMatch"));

                    // Parse candidate results from JSON or other stored format
                    List<ResultRow> candidateResults = parseCandidateResults(rs.getString("resultSummary"));

                    // Calculate percentages for each candidate
                    int totalVotes = candidateResults.stream().mapToInt(ResultRow::getVotes).sum();
                    for (ResultRow row : candidateResults) {
                        double percentage = totalVotes > 0 ? (row.getVotes() * 100.0 / totalVotes) : 0.0;
                        row.setPercentage(percentage);
                    }

                    result.setCandidateResults(candidateResults);
                    return result;
                }
            }
        }
        return null;
    }

    private List<ResultRow> parseCandidateResults(String resultSummary) {
        if (resultSummary == null || resultSummary.trim().isEmpty()) {
            return new ArrayList<>(); // Return empty list if resultSummary is null or empty
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Map<String, Object>> candidateResultsMap = objectMapper.readValue(resultSummary, Map.class);

            List<ResultRow> candidateResults = new ArrayList<>();

            for (Map.Entry<String, Map<String, Object>> entry : candidateResultsMap.entrySet()) {
                String candidateName = entry.getKey();
                Map<String, Object> resultData = entry.getValue();

                ResultRow row = new ResultRow();
                row.setCandidateName(candidateName);  
                row.setVotes(((Number) resultData.get("votes")).intValue());  
                Object percentageValue = resultData.get("percentage");
                if (percentageValue instanceof Number) {
                    row.setPercentage(((Number) percentageValue).doubleValue());
                } else {
                    row.setPercentage(0.0);
                }

                candidateResults.add(row);
            }

            int totalVotes = candidateResults.stream().mapToInt(ResultRow::getVotes).sum();
            for (ResultRow row : candidateResults) {
                double percentage = totalVotes > 0 ? (row.getVotes() * 100.0 / totalVotes) : 0.0;
                String formattedPercentage = String.format("%.2f", percentage);
                row.setPercentage(Double.parseDouble(formattedPercentage));
            }

            return candidateResults;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
