package com.example.servlets;

import com.example.models.Software;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class RequestServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/user_access_management";
    private static final String DB_USER = "uam_user";
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if the user is logged in and has the role of Employee
        HttpSession session = request.getSession(false);
        if (session == null || "Manager".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Unauthorized: Only employees or admins can request access.");
            return;
        }

        // Retrieve form data
        int softwareId = Integer.parseInt(request.getParameter("softwareId"));
        String accessType = request.getParameter("accessType");
        String reason = request.getParameter("reason");
        int userId = (int) session.getAttribute("userId");  //userId is stored in the session

        // Store the request in the "requests" table with status "Pending"
        try {
            // Load PostgreSQL driver explicitly
            Class.forName("org.postgresql.Driver");

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "INSERT INTO requests (user_id, software_id, access_type, reason, status) VALUES (?, ?, ?, ?, 'Pending')";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, softwareId);
                preparedStatement.setString(3, accessType);
                preparedStatement.setString(4, reason);

                // Execute the insert
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    response.getWriter().println("Request added successfully.");
                    response.sendRedirect(request.getContextPath() + "/requestAccess");
                } else {
                    response.getWriter().println("Error: Access request failed.");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("Database error: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if the user is logged in and has the role of Employee
        HttpSession session = request.getSession(false);
        if (session == null || "Manager".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Unauthorized: Only employees or admins can request access.");
            return;
        }

        // Retrieve the list of available software from the database to populate the dropdown
        List<Software> softwareList = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "SELECT id, name, access_levels FROM software";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    List<String> accessLevels = List.of(resultSet.getString("access_levels").split(","));
                    softwareList.add(new Software(id, name, accessLevels));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("Database error: " + e.getMessage());
        }

        // Set the software list as a request attribute and forward to the JSP
        request.setAttribute("softwareList", softwareList);
        request.getRequestDispatcher("/jsp/requestAccess.jsp").forward(request, response);
    }
}
