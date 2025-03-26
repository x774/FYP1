<%-- 
    Document   : summaryReport
    Created on : Nov 20, 2024, 5:46:30 PM
    Author     : joonx
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="dao.*" %>
<%@ page import="models.*" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="../css/styles.css" rel="stylesheet" type="text/css"/>
        <title>Summary Report</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f9f9f9;
                margin: 0;
                padding: 20px;
            }
            .container {
                max-width: 800px;
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
            .result-table {
                margin-top: 10px;
                border-collapse: collapse;
                width: 100%;
            }
            .result-table, th, td {
                border: 1px solid #ddd;
            }
            th, td {
                padding: 10px;
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
            
            <h1>Voting Summary Report</h1>
            <div class="section">
                <h2>1. Voting Activity Overview</h2>
                <p><strong>Activity Name:</strong> <%= request.getAttribute("activityName")%></p>
                <p><strong>Total Valid Votes:</strong> <%= request.getAttribute("totalValidVotes")%></p>
            </div>

            <div class="section">
                <h2>2. Voting Results</h2>
                <table class="result-table">
                    <tr>
                        <th>Candidate</th>
                        <th>Votes</th>
                        <th>Percentage</th>
                    </tr>
                    <%
                        List<ResultRow> results = (List<ResultRow>) request.getAttribute("votingResults");
                        for (ResultRow row : results) {
                    %>
                    <tr>
                        <td><%= row.getCandidateName()%></td> 
                        <td><%= row.getVotes()%></td>
                        <td><%= String.format("%.2f", row.getPercentage()) %> %</td>

                    </tr>
                    <%
                        }
                    %>
                </table>
            </div>

            <div class="section">
                <h2>3. Voting Process Statistics</h2>
                <p><strong>Voting Start Date:</strong> <%= request.getAttribute("startTime")%></p>
                <p><strong>Voting End Date:</strong> <%= request.getAttribute("endTime")%></p>
            </div>
        </div>
    </body>
</html>