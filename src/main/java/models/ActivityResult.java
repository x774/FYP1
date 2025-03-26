/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.util.List;

/**
 *
 * @author joonx
 */
public class ActivityResult {

    private int resultID;
    private int activityID;
    private int totalVotes;
    private Integer winnerCandidateID;
    private String winnerName;
    private String resultSummary;
    private long homomorphicTime;
    private long decryptionTime;
    private boolean resultsMatch;

    private List<ResultRow> candidateResults;

    public ActivityResult() {
    }

    public ActivityResult(int activityID, int totalVotes, Integer winnerCandidateID, String winnerName, String resultSummary) {
        this.activityID = activityID;
        this.totalVotes = totalVotes;
        this.winnerCandidateID = winnerCandidateID;
        this.winnerName = winnerName;
        this.resultSummary = resultSummary;
    }

    public int getResultID() {
        return resultID;
    }

    public void setResultID(int resultID) {
        this.resultID = resultID;
    }

    public int getActivityID() {
        return activityID;
    }

    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }

    public Integer getWinnerCandidateID() {
        return winnerCandidateID;
    }

    public void setWinnerCandidateID(Integer winnerCandidateID) {
        this.winnerCandidateID = winnerCandidateID;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public String getResultSummary() {
        return resultSummary;
    }

    public void setResultSummary(String resultSummary) {
        this.resultSummary = resultSummary;
    }

    public long getHomomorphicTime() {
        return homomorphicTime;
    }

    public void setHomomorphicTime(long homomorphicTime) {
        this.homomorphicTime = homomorphicTime;
    }

    public long getDecryptionTime() {
        return decryptionTime;
    }

    public void setDecryptionTime(long decryptionTime) {
        this.decryptionTime = decryptionTime;
    }

    public boolean isResultsMatch() {
        return resultsMatch;
    }

    public void setResultsMatch(boolean resultsMatch) {
        this.resultsMatch = resultsMatch;
    }

    public List<ResultRow> getCandidateResults() {
        return candidateResults;
    }

    public void setCandidateResults(List<ResultRow> candidateResults) {
        this.candidateResults = candidateResults;
    }
}
