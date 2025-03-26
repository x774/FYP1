<%-- 
    Document   : organizerActivities
    Created on : Nov 22, 2024, 5:39:37 AM
    Author     : joonx
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="models.*" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Organizer Activities</title>
        <link href="<%= request.getContextPath()%>/css/styles.css" rel="stylesheet" type="text/css"/>
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f4f4f4;
                margin: 0;
                padding: 0;
            }

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
                text-align: center;
                font-size: 28px;
                margin-bottom: 20px;
            }

            .voting-details {
                background-color: #f0f0f0;
                padding: 15px;
                border-radius: 5px;
                color: #555;
                font-size: 16px;
                margin-bottom: 20px;
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
        </style>
    </head>
    <body>
        <div class="container">
            <% User user = (User) session.getAttribute("user");
                if (user != null) {%>
            <a href="pages/dashboard.jsp" class="back-button">← Back</a>
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

            <h1>Your Voting Activities</h1>
            <% List<VotingActivity> activities = (List<VotingActivity>) request.getAttribute("activities"); %>
            <% if (activities != null && !activities.isEmpty()) { %>
            <table border="1">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Start Date</th>
                        <th>End Date</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (VotingActivity activity : activities) {%>
                    <tr>
                        <td><%= activity.getVotingActivityName()%></td>
                        <td><%= activity.getStartTime()%></td>
                        <td><%= activity.getEndTime()%></td>
                        <td><%= activity.getStatus()%></td>
                        <td>
                            <% if ("close".equalsIgnoreCase(activity.getStatus())) {%>
                            <a href="generateReport" style="background-color: blue; display: inline-block; padding: 10px; color: white; text-decoration: none; margin-bottom: 5px;">View Report</a>
                            <% } else {%>
                            <form action="edit-activity" method="GET" style="display: inline;">
                                <input type="hidden" name="activityID" value="<%= activity.getVotingActivityID()%>" />
                                <button type="submit" style="margin-bottom: 5px; background-color: #93A9D1; ">View/Edit</button>
                            </form>
                            <form action="closeVote" method="POST" style="display: inline;">
                                <input type="hidden" name="activityID" value="<%= activity.getVotingActivityID()%>" />
                                <input type="password" name="privateKey" required placeholder="Provide your private key here to close vote" 
                                       style="width: 90%; margin-top: 10px; margin-bottom: 2px" />
                                <button type="submit" style="background-color: #F7CACA;">Close Vote</button>
                            </form>
                            <% } %>
                        </td>
                    </tr>
                    <% }
                        out.println("Activities you held: " + activities.size());%>
                </tbody>
            </table>
            <% } else { %>
            <p>No voting activities found. <a href="setupVotingActivity.jsp">Create a new activity</a>.</p>
            <% }%>
        </div>
    </body>
</html>