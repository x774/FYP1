<%-- 
    Document   : editViewActivity
    Created on : Nov 22, 2024, 2:37:58 PM
    Author     : joonx
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="models.*" %>
<%@ page import="services.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>View/Edit Voting Activity</title>
        <link href="<%= request.getContextPath()%>/css/styles.css" rel="stylesheet" type="text/css"/>
        <style>
            .container {
                width: 80%;
                max-width: 800px;
                margin: 50px auto;
                background-color: #fff;
                padding: 20px;
                border-radius: 8px;
                box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            }

            h1 {
                color: #007BFF;
                font-size: 28px;
                margin-bottom: 20px;
            }
            form {
                display: flex;
                flex-direction: column;
            }

            label {
                font-weight: bold;
                margin-top: 15px;
                margin-bottom: 5px;
            }

            input[type="text"],
            input[type="date"],
            textarea {
                width: 90%;
                padding: 10px;
                margin-bottom: 10px;
                border: 1px solid #ccc;
                border-radius: 5px;
                font-size: 14px;
            }

            textarea {
                resize: vertical;
                min-height: 80px;
            }

            button {
                display: block;
                width: 200px;
                margin: 0 auto;
                padding: 15px;
                background-color: #93A9D1;
                color: white;
                font-size: 18px;
                border: none;
                border-radius: 5px;
                cursor: pointer;
                transition: background-color 0.3s;
            }

            button:hover {
                background-color: #0056b3;
            }

            #candidates-table {
                width: 90%;
                border-collapse: collapse;
                margin-top: 15px;
            }

            #candidates-table th,
            #candidates-table td {
                padding: 10px;
                text-align: left;
                border: 1px solid #ddd;
            }

            #candidates-table th {
                background-color: #f1f1f1;
                font-weight: bold;
            }

            #candidates-table tr:nth-child(even) {
                background-color: #f9f9f9;
            }

            #candidates-table tr:hover {
                background-color: #f1f1f1;
            }

            #candidates-table input[type="text"] {
                width: calc(100% - 20px);
                padding: 5px;
            }

            button[type="button"] {
                background-color: #28a745;
                margin-top: 10px;
            }

            button[type="button"]:hover {
                background-color: #218838;
            }

            button.remove-btn {
                background-color: #dc3545;
                color: white;
            }

            button.remove-btn:hover {
                background-color: #c82333;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <% User user = (User) session.getAttribute("user");
                if (user != null) {%>
            <a href="organizer-activities" class="back-button">← Back</a>
            <form action="logout" method="GET" style="margin: 0;">
                <button class="logout-btn" style="background-color: red">
                    Logout (<%= user.getEmail()%>)
                </button>
            </form>
            <% } else { %>
            <script>
                alert("Session expired. Please log in again.");
                window.location = "login.jsp";
            </script>
            <% } %>
            <%
                String activityID = request.getParameter("activityID");
                VotingActivity activity = (VotingActivity) request.getAttribute("activity");
                List<Candidate> candidates = (List<Candidate>) request.getAttribute("candidates");

                if (activity == null) {
                    out.println("Activity not found.");
                    return;
                }
            %>
            <h1>View / Edit Voting Activity</h1>
            <form action="edit-activity" method="post">
                <input type="hidden" name="activityID" value="<%= activity.getVotingActivityID()%>" />

                <label for="activity-name">Voting Activity Name</label>
                <input type="text" id="activity-name" name="activity_name" 
                       value="<%= activity.getVotingActivityName()%>" required>

                <label for="activity-description">Voting Activity Description</label>
                <textarea id="activity-description" name="activity_description"><%= activity.getVotingDescription()%></textarea>

                <label for="start-date">Start Date</label>
                <input type="date" id="start-date" name="start_date" 
                       value="<%= activity.getStartTime()%>" readonly>

                <label for="end-date">End Date</label>
                <input type="date" id="end-date" name="end_date" 
                       value="<%= activity.getEndTime()%>" readonly>

                <label for="allowed-domains">Allowed Email Domains</label>
                <input type="text" id="allowed-domains" name="allowed_domains" 
                       value="<%= activity.getAllowedEmailDomains()%>"
                       placeholder="e.g., example.com, anotherdomain.com">

                <h3>Candidates/Options</h3>
                <table id="candidates-table">
                    <tr>
                        <th>Candidate Name</th>
                        <th>Actions</th>
                    </tr>
                    <%
                        if (candidates != null) {
                            for (Candidate candidate : candidates) {
                                boolean isExistingCandidate = true;
%>
                    <tr>
                        <td>
                            
                            <input type="text" name="candidate_name[]" 
                                   value="<%= candidate.getCandidateName()%>" 
                                   <%= isExistingCandidate ? "readonly" : ""%> 
                                   required style="background-color:gainsboro">
                        </td>
                        <td><button type="button" style="background-color: red" onclick="removeRow(this)">Remove</button></td>
                    </tr>
                    <%
                            }
                        }
                    %>
                </table>
                <button type="button" style="margin-bottom: 10px;" onclick="addCandidateRow()">Add Candidate</button>

                <button type="submit" style="width: 40%">Update Voting Activity</button>
            </form>

            <h3>To View Current Result</h3>
            <form method="POST" action="viewResult">
                <label for="privateKey">Enter Paillier Private Key:</label>
                <input type="text" id="privateKey" name="privateKey" required>
                <input type="hidden" name="activityID" value="<%= activity.getVotingActivityID()%>" />
                <button type="submit">View</button>
            </form>

            <script>
                function addCandidateRow() {
                    const table = document.getElementById("candidates-table");
                    const newRow = table.insertRow();
                    newRow.innerHTML = `
                        <td><input type="text" name="candidate_name[]" required></td>
                        <td><button type="button" style="background-color: red" onclick="removeRow(this)">Remove</button></td>
                    `;
                }

                function removeRow(button) {
                    const row = button.parentElement.parentElement;
                    row.remove();
                }
            </script>
        </div>
    </body>
</html>