# User Access Management System

This is a User Access Management System built using PostgreSQL, JSP, Tomcat, and Maven. Follow the instructions below to set up the project on your local machine.

## Prerequisites

Before you begin, ensure you have the following installed on your machine:

1. **Java**: Make sure you have Java installed. Set the `JAVA_HOME` environment variable to your Java installation path.
2. **Maven**: Install Maven and set the `MAVEN_HOME` environment variable to your Maven installation path.
3. **Tomcat**: Download and install Tomcat. Set the `CATALINA_HOME` environment variable to your Tomcat installation path.
4. **PostgreSQL**: Install PostgreSQL and create a database for the application. Make sure to set your PostgreSQL database password in the system environment variable.

## Installation Steps

1. Clone the Repository

2. Set Environment Variables. Add the PostgreSQL database password to your system's environment variables. The variable should be named DB_PASSWORD.
Install PostgreSQL Database

3. Ensure that PostgreSQL is running and the required database is created.

4. Build the WAR File. Open a terminal and navigate to the project directory. Run the following command to build the WAR file
mvn clean package

5. Deploy the WAR File

6. Copy the generated WAR file from the target directory to the webapps directory of your Tomcat installation. %CATALINA_HOME/webapps/<your-war-file>

7. Start Tomcat Server. Navigate to the bin directory of your Tomcat installation and start the server:
catalina.bat start   # On Windows

8.Access the Application. Open your web browser and go to:
http://localhost:8080/<your-war-file>
