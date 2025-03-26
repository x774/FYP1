<%-- 
    Document   : participantVotes
    Created on : Nov 22, 2024, 5:39:48 AM
    Author     : joonx
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@ page import="models.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Voting Activity</title>
        <link href="<%= request.getContextPath()%>/css/styles.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript">
            <%
                String error = (String) session.getAttribute("error");
                if (error != null && !error.isEmpty()) {
            %>
            alert("<%= error%>");
            <%
                session.removeAttribute("error");
            %>
            <%
                }
            %>
        </script>
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
            <%
                User user = (User) session.getAttribute("user");
                if (user != null) {
            %>
            <a href="pages/dashboard.jsp" class="back-button">← Back</a>
            <form action="logout" method="GET" style="margin: 0;">
                <button class="logout-btn" style="background-color: red">Logout (<%= user.getEmail()%>)</button>
            </form>
            <% } else { %>
            <script>
                alert("Session expired. Please log in again.");
                window.location = "login.jsp";
            </script>
            <% }%>
            <h1>Available Voting Activities</h1>
            <%
                List<Map<String, Object>> activities = (List<Map<String, Object>>) request.getAttribute("votingActivities");
                if (activities != null && !activities.isEmpty()) {
                    for (Map<String, Object> activity : activities) {
            %>
            <div class="voting-details">
                <p><strong>Voting Activity:</strong></p>
                <p><strong>Name:</strong> <%= activity.get("name")%></p>
                <p><strong>Description:</strong> <%= activity.get("description")%></p>
                <p><strong>Voting Time:</strong> <%= activity.get("startTime")%> to <%= activity.get("endTime")%></p>
                <form action="voteNow" method="post">
                    <input type="hidden" name="activityId" value="<%= activity.get("id")%>" />
                    <button type="submit">Vote Now</button>
                </form>
            </div>
            <% }
            } else {%>
            <p>No voting activities found.</p>
            <% }%>

        </div>

    </body>
</html>