package com.example.servlets;

import com.example.models.PendingRequest;
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


public class ApprovalServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/user_access_management";
    private static final String DB_USER = "uam_user";
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if the user is logged in and has the role of Manager
        HttpSession session = request.getSession(false);
        if (session == null || "Employee".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Unauthorized: Only managers or admins can approve or reject requests.");
            return;
        }

        // Retrieve form data
        int requestId = Integer.parseInt(request.getParameter("requestId"));
        String action = request.getParameter("action"); // "Approve" or "Reject"

        // Determine the new status based on the action
        String newStatus = "Pending";
        if ("Approve".equals(action)) {
            newStatus = "Approved";
        } else if ("Reject".equals(action)) {
            newStatus = "Rejected";
        }

        // Update the status of the request in the database
        try {
            // Load PostgreSQL driver explicitly
            Class.forName("org.postgresql.Driver");

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "UPDATE requests SET status = ? WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, newStatus);
                preparedStatement.setInt(2, requestId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    // Redirect to the pending requests page after successful approval/rejection
                    response.sendRedirect(request.getContextPath() + "/pendingRequests");
                } else {
                    response.getWriter().println("Error: Failed to update the request status.");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("Database error: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if the user is logged in and has the role of Manager
        HttpSession session = request.getSession(false);
        if (session == null || "Employee".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Unauthorized: Only managers or admins can view pending requests.");
            return;
        }

        // Retrieve the list of pending requests from the database
        List<PendingRequest> pendingRequests = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "SELECT r.id, u.username AS employee_name, s.name AS software_name, r.access_type, r.reason " +
                        "FROM requests r " +
                        "JOIN users u ON r.user_id = u.id " +
                        "JOIN software s ON r.software_id = s.id " +
                        "WHERE r.status = 'Pending'";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int requestId = resultSet.getInt("id");
                    String employeeName = resultSet.getString("employee_name");
                    String softwareName = resultSet.getString("software_name");
                    String accessType = resultSet.getString("access_type");
                    String reason = resultSet.getString("reason");

                    pendingRequests.add(new PendingRequest(requestId, employeeName, softwareName, accessType, reason));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("Database error: " + e.getMessage());
        }

        // Set the list of pending requests as a request attribute and forward to the JSP
        request.setAttribute("pendingRequests", pendingRequests);
        request.getRequestDispatcher("/jsp/pendingRequests.jsp").forward(request, response);
    }
}
