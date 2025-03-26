<%-- 
    Document   : setupVotingActivity
    Created on : Nov 20, 2024, 5:45:15 PM
    Author     : joonx
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="models.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="../css/styles.css" rel="stylesheet" type="text/css"/>
        <title>Setup Voting Activity</title>
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
            <%
                User user = (User) session.getAttribute("user");
                if (user != null) {
            %>
             <a href="javascript:history.back()" class="back-button">← Back</a>
            <form action="../logout" method="GET" style="margin: 0;">
                <button class="logout-btn" style="background-color: red">Logout (<%= user.getEmail()%>)</button>
            </form>
            <% } else { %>
            <script>
                alert("Session expired. Please log in again.");
                window.location = "login.jsp";
            </script>
            <% }%>
            <h1>Setup Voting Activity</h1>
            <form action="../setup-voting" method="post">
                <label for="activity-name">Voting Activity Name</label>
                <input type="text" id="activity-name" name="activity_name" required>

                <label for="activity-description">Voting Activity Description</label>
                <textarea id="activity-description" name="activity_description"></textarea>

                <label for="start-date">Start Date</label>
                <input type="date" id="start-date" name="start_date" required>

                <label for="end-date">End Date</label>
                <input type="date" id="end-date" name="end_date" required>

                <label for="allowed-domains">Allowed Email Domains</label>
                <input type="text" id="allowed-domains" name="allowed_domains" placeholder="e.g., example.com, anotherdomain.com">

                <h3>Candidates/Options</h3>
                <table id="candidates-table">
                    <tr>
                        <th>Candidate Name</th>
                        <th>Actions</th>
                    </tr>
                    <tr>
                        <td><input type="text" name="candidate_name[]" required></td>
                        <td><button type="button" style="background-color: red" onclick="removeRow(this)">Remove</button></td>
                    </tr>
                </table>
                <button type="button" style=" margin-bottom: 10px;" onclick="addCandidateRow()">Add Candidate</button>

                <button type="submit" style="width: 40%">Setup Voting</button>
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