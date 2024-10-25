package com.example.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;


public class SignUpServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/user_access_management"; // Update with your database URL
    private static final String DB_USER = "uam_user"; // Update with your database username
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD"); // Update with your database password

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Class.forName("org.postgresql.Driver");
            Class.forName("org.apache.commons.logging.LogFactory");
            Class.forName("org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL driver class or BCrypt Encoder class not found!");
        }
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role"); // This will be "Employee"

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);


        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Prepare SQL statement to insert new user
            String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword); // Consider hashing the password before storing
            preparedStatement.setString(3, role);

            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                // Registration successful, redirect to login page
                System.out.println("Registration was successful");
                response.sendRedirect(request.getContextPath() + "/login");
            } else {
                // Handle unsuccessful registration (optional)
                response.getWriter().println("Registration failed. Please try again.");
            }

            // Close resources
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/signup.jsp");
        dispatcher.forward(request, response);
    }
}