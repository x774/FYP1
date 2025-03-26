/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.util.Date;

/**
 *
 * @author joonx
 */
public class VotingActivity {

    private int votingActivityID;
    private int userID;
    private String votingActivityName;
    private String votingDescription;
    private Date startTime;
    private Date endTime;
    private String allowedEmailDomains;
    private String status;
    private String publicKey;
    private String paillierPublicKey;

    public VotingActivity() {
    }

    public int getVotingActivityID() {
        return votingActivityID;
    }

    public void setVotingActivityID(int votingActivityID) {
        this.votingActivityID = votingActivityID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getVotingActivityName() {
        return votingActivityName;
    }

    public void setVotingActivityName(String votingActivityName) {
        this.votingActivityName = votingActivityName;
    }

    public String getVotingDescription() {
        return votingDescription;
    }

    public void setVotingDescription(String votingDescription) {
        this.votingDescription = votingDescription;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getAllowedEmailDomains() {
        return allowedEmailDomains;
    }

    public void setAllowedEmailDomains(String allowedEmailDomains) {
        this.allowedEmailDomains = allowedEmailDomains;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPaillierPublicKey() {
        return paillierPublicKey;
    }

    public void setPaillierPublicKey(String paillierPublicKey) {
        this.paillierPublicKey = paillierPublicKey;
    }

}