<%-- 
    Document   : dashboard
    Created on : Nov 20, 2024, 5:30:48 PM
    Author     : joonx
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page session="true" %>
<%@ page import="java.util.List" %>
<%@ page import="models.*" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="../css/styles.css" rel="stylesheet" type="text/css"/>
        <title>Dashboard</title>
        <style>
            h1, h2 {
                font-family: Arial, sans-serif;
            }
            ul {
                list-style-type: none;
                padding: 0;
            }
            ul li {
                margin: 10px 0;
            }
            ul li a {
                text-decoration: none;
                color: #007BFF;
                font-weight: bold;
            }
            ul li a:hover {
                text-decoration: underline;
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
            <%
                User user = (User) session.getAttribute("user");
                if (user != null) {
            %>
            <form action="../logout" method="GET" style="margin: 0;">
                <button class="logout-btn" style="background-color: red">Logout (<%= user.getEmail()%>)</button>
            </form>
            <% } else { %>
            <script>
                alert("Session expired. Please log in again.");
                window.location = "login.jsp";
            </script>
            <% }%>
            <h1>Welcome to Secure Voting System</h1>
            <h2>Dashboard</h2>
            <ul>
                <li><a href="setupVotingActivity.jsp">Become a Voting Organizer</a></li>
                <li><a href="../organizer-activities">View The Voting Activities Your Organize</a></li>
                <li><a href="../participant-votes">View Available Voting Activities In System</a></li>
                <li><a href="../generateReport">View Result/Report</a></li>
            </ul>
        </div>
    </body>
</html>