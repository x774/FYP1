<%-- 
    Document   : voting
    Created on : Nov 20, 2024, 5:45:56 PM
    Author     : joonx & KOH HUI QING
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="dao.*" %>
<%@ page import="models.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="<%= request.getContextPath()%>/css/styles.css" rel="stylesheet" type="text/css"/>
        <title>Vote</title>
        <style>
            .container {
                background: white;
                padding: 20px;
                border-radius: 8px;
                box-shadow: 0 0 10px rgba(0,0,0,0.1);
                width: 50%;
            }
            h1 {
                margin-bottom: 20px;
                font-size: 24px;
            }
            label {
                display: block;
                margin-bottom: 10px;
                font-weight: bold;
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
            <h1>Vote</h1>
            <%
                int activityId = Integer.parseInt(request.getParameter("activityId"));
                String publicKey = (String) request.getAttribute("publicKey");
                CandidateDAO candidateDAO = new CandidateDAO();
                List<Candidate> candidates = candidateDAO.getCandidatesByActivityId(activityId);
            %>
            <form method="POST" action="submitVote" onsubmit="event.preventDefault();
                    encryptAndSubmitForm();">
                <label>Select Candidate:</label>
                <% for (Candidate candidate : candidates) {%>
                <label>
                    <input type="radio" name="candidateId" value="<%= candidate.getCandidateID()%>" required>
                    <%= candidate.getCandidateName()%>
                </label><br>
                <% }%>
                <input type="hidden" id="encryptedVote" name="encryptedVote">
                <input type="hidden" name="activityId" value="<%= activityId%>">
                <button type="submit">Submit Vote</button>
            </form>
    </body>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jsencrypt/3.0.0/jsencrypt.min.js"></script>
    <script>
                function encryptAndSubmitForm() {
                    console.log("Starting vote submission process...");
                    const publicKey = `<%= publicKey%>`;  // RSA public key
                    console.log("Retrieved RSA public key");
                    const activityId = document.querySelector('input[name="activityId"]').value;
                    const candidateId = document.querySelector('input[name="candidateId"]:checked')?.value;
                    if (!candidateId) {
                        alert("Please select a candidate.");
                        return;
                    }

                    // Log the values for debugging
                    console.log("Activity ID:", activityId);
                    console.log("Selected Candidate ID:", candidateId);

                    // Create a new JSEncrypt object and set the RSA public key
                    const encrypt = new JSEncrypt();
                    encrypt.setPublicKey(publicKey);

                    // Encrypt the candidate ID with RSA public key
                    const encryptedVote = encrypt.encrypt(candidateId);  // This will be the RSA-encrypted candidate ID
                    console.log("Done encrypted using RSA public key:", encryptedVote);

                    if (encryptedVote) {
                        // Send the encrypted vote and activityId to the server
                        const formData = new URLSearchParams();
                        formData.append("encryptedVote", encryptedVote);  // Encrypted data
                        formData.append("activityId", activityId);      // Activity ID
                        formData.append("candidateId", candidateId);
                        console.log("Successfully encrypted vote with RSA ");
                        fetch("submitVote", {
                            method: "POST",
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            body: formData.toString()
                        })
                                .then(async (response) => {
                                    if (!response.ok) {
                                        const errorText = await response.text();
                                        throw new Error(errorText);
                                    }
                                    return response.text();
                                })
                                .then((data) => {
                                    alert("Vote submitted successfully!");
                                    window.location.href = "pages/dashboard.jsp";
                                })
                                .catch((error) => {
                                    console.error("Error details:", error);
                                    alert(`Vote submission failed: ${error.message}`);
                                });
                    }
                }
    </script>
</html>