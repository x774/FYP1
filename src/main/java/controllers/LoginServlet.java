/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

/**
 *
 * @author KOH HUI QING
 */
import dao.UserDAO;
import models.User;
import services.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String encryptedData = request.getParameter("encryptedData");

        try (Connection conn = DatabaseService.getConnection()) {
            if (encryptedData == null || encryptedData.isEmpty()) {
                response.getWriter().write("<script>alert('Invalid request.'); window.history.back();</script>");
                return;
            }

            // Decrypt the received data
            System.out.println("Received encrypted user data: " + encryptedData);
            String decryptedData = RSAUtil.decrypt(encryptedData);
            System.out.println("Decrypted data: " + decryptedData);

            String[] credentials = decryptedData.split(":");
            if (credentials.length != 2) {
                response.getWriter().write("<script>alert('Invalid credentials format.'); window.history.back();</script>");
                return;
            }
            
            String email = credentials[0];
            String password = credentials[1];
            System.out.println("Extracted email: " + email);
            // System.out.println("Extracted password: " + password);

            // Retrieve user by email
            UserDAO userDAO = new UserDAO(conn);
            User user = userDAO.getUserByEmail(email);

            if (user != null) {
                String hashedPassword = HashUtil.hashPassword(password);
                if (hashedPassword.equals(user.getPasswordHash())) {
                    // Check if the user is the admin
                    if ("admin@gmail.com".equals(email) && hashedPassword.equals(user.getPasswordHash())) {
                        // Redirect to the register page if the user is admin
                        System.out.println("Admin user detected. Redirecting to register page.");
                        HttpSession session = request.getSession();
                        session.setAttribute("user", user);
                        response.sendRedirect("pages/register.jsp");
                    } else {
                        // Redirect to the dashboard page for other users
                        System.out.println("Regular user detected. Redirecting to dashboard.");
                        HttpSession session = request.getSession();
                        session.setAttribute("user", user);
                        response.sendRedirect("pages/dashboard.jsp");
                    }
                } else {
                    response.getWriter().write("<script>alert('Invalid email or password.'); window.history.back();</script>");
                }
            } else {
                response.getWriter().write("<script>alert('User not found.'); window.history.back();</script>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("<script>alert('An internal server error occurred.'); window.history.back();</script>");
        }
    }
}
