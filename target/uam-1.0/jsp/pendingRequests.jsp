<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.example.models.PendingRequest" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>Pending Access Requests</title>
</head>
<body>
    <h2>Pending Access Requests</h2>

    <%
        // Retrieve the list of pending requests from the request attribute
        java.util.List<PendingRequest> pendingRequests = (java.util.List<PendingRequest>) request.getAttribute("pendingRequests");
        if (pendingRequests != null && !pendingRequests.isEmpty()) {
    %>
        <table border="1">
            <thead>
                <tr>
                    <th>Employee Name</th>
                    <th>Software Name</th>
                    <th>Access Type</th>
                    <th>Reason</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
            <%
                // Iterate through the pending requests and display each request in a table row
                for (PendingRequest requestObj : pendingRequests) {
                    out.println("<tr>");
                    out.println("<td>" + requestObj.getEmployeeName() + "</td>");
                    out.println("<td>" + requestObj.getSoftwareName() + "</td>");
                    out.println("<td>" + requestObj.getAccessType() + "</td>");
                    out.println("<td>" + requestObj.getReason() + "</td>");
                    out.println("<td>");
                    out.println("<form action='" + request.getContextPath() + "/pendingRequests' method='post' style='display:inline;'>");
                    out.println("<input type='hidden' name='requestId' value='" + requestObj.getRequestId() + "'>");
                    out.println("<input type='submit' name='action' value='Approve'>");
                    out.println("</form>");
                   out.println("<form action='" + request.getContextPath() + "/pendingRequests' method='post' style='display:inline;'>");
                    out.println("<input type='hidden' name='requestId' value='" + requestObj.getRequestId() + "'>");
                    out.println("<input type='submit' name='action' value='Reject'>");
                    out.println("</form>");
                    out.println("</td>");
                    out.println("</tr>");
                }
            %>
            </tbody>
        </table>
    <%
        } else {
            out.println("<p>No pending access requests.</p>");
        }
    %>

</body>
</html>
