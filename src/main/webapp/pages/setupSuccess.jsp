<%-- 
    Document   : setupSuccess
    Created on : Nov 22, 2024, 6:43:18 AM
    Author     : joonx
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="models.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Setup Successful</title>
        <link href="../css/styles.css" rel="stylesheet" type="text/css"/>
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
        <script>
            window.onload = function () {
                const publicKey = '<%= session.getAttribute("publicKey")%>';
                const privateKey = '<%= session.getAttribute("privateKey")%>';
                const paillierPublicKey = '<%= session.getAttribute("paillierPublicKey")%>';
                const paillierPrivateKey = '<%= session.getAttribute("paillierPrivateKey")%>';
                const activityName = '<%= session.getAttribute("activityName")%>';

                const fileName = activityName ? activityName.replace(/\s+/g, '_') + "_keys.txt" : "voting_keys.txt";
                const keyContent = `RSA Public Key:\n${publicKey}\n\nRSA Private Key:\n${privateKey}\n\nPaillier Public Key:\n${paillierPublicKey}\n\nPaillier Private Key:\n${paillierPrivateKey}`;

                // Display the message to the user about the download
                const message = `Your keys are ready for download. The keys being downloaded include:\n\n- RSA Public Key\n- RSA Private Key\n- Paillier Public Key\n- Paillier Private Key(For close vote)`;

                // Show the message to the user
                alert(message);

                // Trigger the download after a short delay (1 second)
                setTimeout(function () {
                    const keyBlob = new Blob([keyContent], {type: 'text/plain'});

                    const link = document.createElement('a');
                    link.href = URL.createObjectURL(keyBlob);
                    link.download = fileName;

                    // Automatically click the link to start the download
                    link.click();

                    // Clean up session attributes after download is triggered
            <% session.removeAttribute("publicKey"); %>
            <% session.removeAttribute("privateKey"); %>
            <% session.removeAttribute("paillierPublicKey"); %>
            <% session.removeAttribute("paillierPrivateKey"); %>
            <% session.removeAttribute("activityName"); %>
                }, 1000);  // Delay the download by 1 second for user awareness
            };
        </script>
    </head>
    <body>
        <div class="container">
            <%
                User user = (User) session.getAttribute("user");
                if (user != null) {
            %>
            <a href="dashboard.jsp" class="back-button">← Back</a>
            <form action="../logout" method="GET" style="margin: 0;">
                <button class="logout-btn" style="background-color: red">Logout (<%= user.getEmail()%>)</button>
            </form>
            <% } else { %>
            <script>
                alert("Session expired. Please log in again.");
                window.location = "login.jsp";
            </script>
            <% }%>
            <h1>Voting Activity Setup Successful</h1>
            <p>Your voting activity has been successfully created!</p>
        </div>
    </body>
</html>