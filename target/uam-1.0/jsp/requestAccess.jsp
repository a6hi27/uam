<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.example.models.Software" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>Request Access to Software</title>
    <script>
        // JavaScript function to update the access levels based on the selected software
        function updateAccessLevels(softwareId) {
            // Hide all access level dropdowns first
            let accessLevelDropdowns = document.querySelectorAll('.access-level-dropdown');
            accessLevelDropdowns.forEach(function(dropdown) {
                dropdown.style.display = 'none';
            });

            // Show the corresponding dropdown for the selected software
            let selectedDropdown = document.getElementById('accessLevels-' + softwareId);
            if (selectedDropdown) {
                selectedDropdown.style.display = 'block';
            }
        }
    </script>
</head>
<body>
    <h2>Request Access</h2>

    <form action="<%= request.getContextPath() %>/requestAccess" method="post">
        <!-- Software Name Dropdown (dynamically populated with server-side code) -->
        <label for="software">Software Name:</label>
        <select id="software" name="softwareId" required onchange="updateAccessLevels(this.value)">
            <option value="" disabled selected>Select a Software</option>
            <%
                // Retrieve software list from request attribute (populated by the servlet)
                java.util.List<Software> softwareList = (java.util.List<Software>) request.getAttribute("softwareList");
                if (softwareList != null) {
                    for (Software software : softwareList) {
                        out.println("<option value=\"" + software.getId() + "\">" + software.getName() + "</option>");
                    }
                }
            %>
        </select><br><br>

        <!-- Access Type Dropdowns (initially hidden, displayed based on selected software) -->
        <%
            // Iterate over each software and create its access levels dropdown
            if (softwareList != null) {
                for (Software software : softwareList) {
                    List<String> accessLevels = software.getAccessLevels();  // Access levels for this software
                    out.println("<select id='accessLevels-" + software.getId() + "' name='accessType' class='access-level-dropdown' style='display:none;' required>");
                    for (String level : accessLevels) {
                        out.println("<option value='" + level + "'>" + level + "</option>");
                    }
                    out.println("</select>");
                }
            }
        %>
        <br><br>

        <!-- Reason for Request -->
        <label for="reason">Reason for Request:</label><br>
        <textarea id="reason" name="reason" rows="5" cols="40" required></textarea><br><br>

        <input type="submit" value="Request Access">
    </form>
</body>
</html>
