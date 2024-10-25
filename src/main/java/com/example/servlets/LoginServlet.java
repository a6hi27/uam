package com.example.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/user_access_management";
    private static final String DB_USER = "uam_user";
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            Class.forName("org.postgresql.Driver");
            Class.forName("org.apache.commons.logging.LogFactory");
            Class.forName("org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL driver class not found or BCrypt Encoder class not found!");
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // SQL query to retrieve the user's hashed password and role by username
        String sql = "SELECT id, role, password FROM users WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // User exists, retrieve hashed password and role
                    String storedHashedPassword = resultSet.getString("password");
                    String role = resultSet.getString("role");
                    int userId = resultSet.getInt("id");

                    // Verify the password using BCrypt
                    if (passwordEncoder.matches(password, storedHashedPassword)) {
                        // Valid credentials; manage user session
                        HttpSession session = request.getSession();
                        session.setAttribute("username", username);
                        session.setAttribute("role", role);
                        session.setAttribute("userId", userId);

                        // Redirect based on user role
                        switch (role) {
                            case "Employee":
                                response.sendRedirect(request.getContextPath() + "/requestAccess");
                                break;
                            case "Manager":
                                response.sendRedirect(request.getContextPath() + "/pendingRequests");
                                break;
                            case "Admin":
                                response.sendRedirect(request.getContextPath() + "/createSoftware");
                                break;
                            default:
                                response.getWriter().println("Invalid role.");
                        }
                    } else {
                        // Invalid password
                        response.getWriter().println("Invalid password.");
                    }
                } else {
                    // Username not found
                    response.getWriter().println("User not found");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/login.jsp");
        dispatcher.forward(request, response);
    }
}