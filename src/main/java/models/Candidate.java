/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author joonx
 */
public class Candidate {
    private int candidateID;
    private int votingActivityID;
    private String candidateName;

    public Candidate() {
    }

    public Candidate(int candidateID, int votingActivityID, String candidateName) {
        this.candidateID = candidateID;
        this.votingActivityID = votingActivityID;
        this.candidateName = candidateName;
    }
    
    public int getCandidateID() {
        return candidateID;
    }

    public void setCandidateID(int candidateID) {
        this.candidateID = candidateID;
    }

    public int getVotingActivityID() {
        return votingActivityID;
    }

    public void setVotingActivityID(int votingActivityID) {
        this.votingActivityID = votingActivityID;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }
    
}