package com.example.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.StringJoiner;


public class SoftwareServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/user_access_management";
    private static final String DB_USER = "uam_user";
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"Admin".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Unauthorized: You do not have access to this resource.");
            return;
        }

        // Get the software details from the form
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String[] accessLevels = request.getParameterValues("accessLevel");

        // Join the selected access levels into a comma-separated string
        StringJoiner accessLevelsJoiner = new StringJoiner(",");
        if (accessLevels != null) {
            for (String level : accessLevels) {
                accessLevelsJoiner.add(level);
            }
        }

        // Database insert logic
        try {
            // Load PostgreSQL driver explicitly
            Class.forName("org.postgresql.Driver");

            // Establish connection to the database
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                // Prepare the SQL insert statement
                String sql = "INSERT INTO software (name, description, access_levels) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, description);
                preparedStatement.setString(3, accessLevelsJoiner.toString());

                // Execute the SQL update
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    // Redirect to a success page or the admin dashboard
                    response.sendRedirect(request.getContextPath() + "/createSoftware");
                } else {
                    // Handle unsuccessful software creation
                    response.getWriter().println("Error: Software creation failed.");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("Database error: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"Admin".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Unauthorized: You do not have access to this resource.");
            return;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/createSoftware.jsp");
        dispatcher.forward(request, response);
    }
}
