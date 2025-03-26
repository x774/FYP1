/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

/**
 *
 * @author joonx
 */
import dao.UserDAO;
import models.User;
import services.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String encryptedData = request.getParameter("encryptedData");

        try (Connection conn = DatabaseService.getConnection()) {
            if (encryptedData == null || encryptedData.isEmpty()) {
                response.getWriter().write("<script>alert('Invalid request.'); window.history.back();</script>");
                return;
            }

            // Decrypt the received data
            System.out.println("Received encrypted data: " + encryptedData);
            String decryptedData = RSAUtil.decrypt(encryptedData);
            System.out.println("Decrypted data: " + decryptedData);

            String[] credentials = decryptedData.split(":");
            if (credentials.length != 2) {
                response.getWriter().write("<script>alert('Invalid credentials format.'); window.history.back();</script>");
                return;
            }

            String email = credentials[0];
            String password = credentials[1];
            System.out.println("Email: " + email);
            // System.out.println("Password: " + password);

            // Create user object
            User user = new User();
            user.setEmail(email);
            user.setPasswordHash(password);

            // Add user to database
            UserDAO userDAO = new UserDAO(conn);
            System.out.println("Adding user to the database: " + email);
            userDAO.addUser(user);

            // Registration successful
            System.out.println("User registered successfully: " + email);
            response.getWriter().write("<script>alert('Registration successful!'); window.location='pages/register.jsp';</script>");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("<script>alert('Registration failed. Please try again.'); window.history.back();</script>");
        }
    }
}
