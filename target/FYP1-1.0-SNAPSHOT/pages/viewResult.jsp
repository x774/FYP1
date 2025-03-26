<%-- 
    Document   : viewResult
    Created on : Nov 24, 2024, 8:32:16 AM
    Author     : joonx
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page import="dao.*" %>
<%@ page import="models.*" %>
<%
    String activityName = (String) session.getAttribute("activityName");
    if (activityName == null) {
        activityName = "Unknown Activity";
    }
    Map<Integer, Integer> candidateVotes = (Map<Integer, Integer>) session.getAttribute("candidateVotes");
    Map<Integer, String> candidateNames = (Map<Integer, String>) session.getAttribute("candidateNames");
    Map<Integer, Double> candidatePercentages = (Map<Integer, Double>) session.getAttribute("candidatePercentages");
    Integer totalVotes = (Integer) session.getAttribute("totalVotes");
    Integer activityID = (Integer) session.getAttribute("activityID");
    Map<Integer, String> topCandidates = (Map<Integer, String>) session.getAttribute("topCandidates");
    Integer maxVotes = (Integer) session.getAttribute("maxVotes");
    if (candidateVotes == null || candidateNames == null || candidatePercentages == null || totalVotes == null) {
        out.println("<h3>Error: Result data is missing. Please try again.</h3>");
        return;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <title>Current Result</title>
        <link href="../css/styles.css" rel="stylesheet" type="text/css"/>
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 20px;
            }
            table {
                width: 80%;
                border-collapse: collapse;
                margin: 20px 0;
            }
            table, th, td {
                border: 1px solid #ddd;
            }
            th, td {
                padding: 8px;
                text-align: center;
            }
            th {
                background-color: #f4f4f4;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <%
                User user = (User) session.getAttribute("user");
                if (user != null) {
            %>
            <a href="../edit-activity?activityID=<%= activityID%>" class="back-button">← Back</a>
            <form action="../logout" method="GET" style="margin: 0;">
                <button class="logout-btn" style="background-color: red">Logout (<%= user.getEmail()%>)</button>
            </form>
            <% } else { %>
            <script>
                alert("Session expired. Please log in again.");
                window.location = "login.jsp";
            </script>
            <% }%>
            <h1>Current <%= session.getAttribute("activityName")%>'s Voting Results</h1>
            <h3>Total Votes: <%= totalVotes%></h3>
            <table>
                <thead>
                    <tr>
                        <th>Candidate</th>
                        <th>Votes</th>
                        <th>Percentage</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Integer candidateID : candidateVotes.keySet()) {%>
                    <tr>
                        <td><%= candidateNames.get(candidateID)%></td>
                        <td><%= candidateVotes.get(candidateID)%></td>
                        <td><%= String.format("%.2f", candidatePercentages.get(candidateID))%> %</td>
                    </tr>
                    <% }%>
                </tbody>
            </table>
            <h2>Highest Votes</h2>
            <% if (topCandidates != null && !topCandidates.isEmpty()) {%>
            <p>The candidate(s) with the highest votes (<%= maxVotes%>) are:</p>
            <ul>
                <% for (Map.Entry<Integer, String> entry : topCandidates.entrySet()) {%>
                <li><%= entry.getValue()%></li>
                    <% } %>
            </ul>
            <% } else { %>
            <p>No votes have been cast yet.</p>
            <% }%>
        </div>
    </body>
</html>
