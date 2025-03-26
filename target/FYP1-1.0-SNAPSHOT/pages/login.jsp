<%-- 
    Document   : login
    Created on : Nov 20, 2024, 5:44:00 PM
    Author     : KOH HUI QING
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
                margin: 0;
                background-color: #f0f0f0;
            }
            .container {
                background: white;
                padding: 50px;
                border-radius: 8px;
                box-shadow: 0 0 10px rgba(0,0,0,0.1);
                width: 300px;
                text-align: center;
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
            input[type="text"],
            input[type="password"] {
                width: 100%;
                padding: 10px;
                margin-bottom: 20px;
                border: 1px solid #ddd;
                border-radius: 4px;
            }
            button {
                width: 100%;
                padding: 10px;
                background-color: #007BFF;
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;
            }
            button:hover {
                background-color: #0056b3;
            }
        </style>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jsencrypt/3.0.0-beta.1/jsencrypt.min.js"></script>
        <script>
            const publicKey = `
                -----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTEdLBDu6SHR38zfWMhjm3k43e3EBLhlFXKYS9Yb+7k3bR4Cl5raQcP4bLTLnyEqXkBM1rCrihbYHUV5o3Y0TF4zfKJ0t1OVPy2of6NMTPHTJLZMd9vtDjmXseEan874/EvjI9DkyuPLnRBZirsxcvkaxFyUau/Mcm9ntzH3JDFwIDAQAB
-----END PUBLIC KEY-----
            `;

            function encryptForm() {
                const encrypt = new JSEncrypt();
                encrypt.setPublicKey(publicKey);
                console.log("Retrieved RSA public key");
                
                const email = document.getElementById("email").value;
                const password = document.getElementById("password").value;

                const encryptedData = encrypt.encrypt(email + ":" + password);
                if (!encryptedData) {
                    alert("Encryption failed.");
                    return false;
                }
                console.log("Successfully encrypted user with RSA ");

                document.getElementById("encryptedData").value = encryptedData;

                // Clear plaintext fields to ensure only encrypted data is sent
                document.getElementById("email").value = "";
                document.getElementById("password").value = "";
                return true;
            }
        </script>
    </head>
    <body>
        <div class="container">
            <h1>Login</h1>
            <form method="POST" action="../login" onsubmit="return encryptForm()">
                <label for="email">Email:</label>
                <input type="text" id="email" name="email" required>

                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>

                <input type="hidden" id="encryptedData" name="encryptedData">
                <button type="submit">Login</button>
            </form>
        </div>
    </body>
</html>
