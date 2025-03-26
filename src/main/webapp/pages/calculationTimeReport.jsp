<%-- 
    Document   : calculationTimeReport
    Created on : Nov 20, 2024, 5:46:50 PM
    Author     : joonx
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="dao.ActivityResultDAO" %>
<%@ page import="models.ActivityResult" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="../css/styles.css" rel="stylesheet" type="text/css"/>
        <title>Voting Result Calculation Time Report</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f4f4f4;
                margin: 0;
                padding: 20px;
            }
            .container {
                max-width: 900px;
                margin: auto;
                background: #fff;
                padding: 20px;
                box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            }
            h1 {
                text-align: center;
                color: #333;
            }
            h2 {
                color: #444;
                margin-top: 20px;
                border-bottom: 2px solid #ddd;
                padding-bottom: 10px;
            }
            p {
                line-height: 1.6;
            }
            .section {
                margin-bottom: 20px;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 10px;
            }
            table, th, td {
                border: 1px solid #ddd;
            }
            th, td {
                padding: 8px;
                text-align: left;
            }
            th {
                background-color: #f4f4f4;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <a href="generateReport" class="back-button">← Back</a>
            <%
                Long homomorphicTime = (Long) request.getAttribute("homomorphicTime");
                Long decryptionTime = (Long) request.getAttribute("decryptionTime");
                Boolean resultsMatch = (Boolean) request.getAttribute("resultsMatch");
            %>
            <h1>Voting Result Calculation Time Report</h1>
            <div class="section">
                <h2>1. Voting Activity Overview</h2>
                <p><strong>Activity Name:</strong> <%= request.getAttribute("activityName")%></p>
                <p><strong>Total Votes:</strong> <%= request.getAttribute("totalVotes")%></p>
            </div>

            <div class="section">
                <h2>2. Total Vote Counting Time to Final Result</h2>
                <p><strong>Homomorphic Paillier Addition Method (sec):</strong> 
                    <%= homomorphicTime / 1e9 %></p>
                <p><strong>Decryption Individual Vote Method [normal way] (sec):</strong> 
                    <%= decryptionTime / 1e9 %></p>
            </div>

            <div class="section">
                <h2>3. Verification Match</h2>
                <p><strong>Results Match:</strong> 
                    <%= resultsMatch %></p>
            </div>
        </div>
    </body>
</html>