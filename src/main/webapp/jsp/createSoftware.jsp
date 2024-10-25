<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
    <title>Create Software</title>
</head>
<body>
    <h2>Create New Software</h2>

    <form action="${pageContext.request.contextPath}/createSoftware" method="post">
        <label for="name">Software Name:</label>
        <input type="text" id="name" name="name" required><br><br>

        <label for="description">Description:</label><br>
        <textarea id="description" name="description" rows="5" cols="40"></textarea><br><br>

        <label>Access Levels:</label><br>
        <input type="checkbox" name="accessLevel" value="Read"> Read<br>
        <input type="checkbox" name="accessLevel" value="Write"> Write<br>
        <input type="checkbox" name="accessLevel" value="Admin"> Admin<br><br>

        <input type="submit" value="Create Software">
    </form>
</body>
</html>
