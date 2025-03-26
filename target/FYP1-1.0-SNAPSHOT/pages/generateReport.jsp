<%-- 
    Document   : reportHistory
    Created on : Nov 22, 2024, 9:32:13 PM
    Author     : joonx
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="dao.VotingActivityDAO" %>
<%@ page import="models.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Report</title>
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

            label {
                display: block;
                margin-bottom: 10px;
                font-weight: bold;
            }
            input[type="date"],
            select {
                width: 100%;
                padding: 10px;
                margin-bottom: 20px;
                border: 1px solid #ddd;
                border-radius: 4px;
            }
            button {
                display: block;
                width: 200px;
                margin: 0 auto;
                padding: 15px;
                background-color: #007BFF;
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
            <a href="/FYP1/pages/dashboard.jsp" class="back-button">← Back</a>
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
            <h1>Generate Report</h1>
            <form action="generateReport" method="post">
                <label for="report-type">Report Type</label>
                <select id="report-type" name="report_type" required>
                    <option value="summary">Summary Report</option>
                    <option value="voting-process">Voting Calculation Time Report</option>
                </select>

                <label for="votingActivity">Select Voting Activity:</label>
                <select id="votingActivity" name="votingActivity" required>
                    <%
                        List<VotingActivity> closedActivities = (List<VotingActivity>) request.getAttribute("closedActivities");
                        if (closedActivities != null) {
                            for (VotingActivity activity : closedActivities) {
                    %>
                    <option value="<%= activity.getVotingActivityID()%>">
                        <%= activity.getVotingActivityName()%>
                    </option>
                    <%
                            }
                        }%>
                </select>

                <button type="submit">Generate Report</button>
            </form>
        </div>
    </body>
</html>