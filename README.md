# Narwhal Notes API
The API managing all backend data and which allows front-end Service Accounts to interact with a centralized server component.

Written in the Kotlin programming language, using Maven as 
a dependency manager, and the Spring Boot Web
Application Framework.

## Project Structure 

API is broken up into several pieces:
- Public Endpoints: To access general information about the API or Project Fawkes.
- Registration and Authentication: To register a new user or let an existing user log in.
- Admin or General User Endpoints: Allows a user to create, retrieve, update, or delete data from their account, including profile data and note data. Admins have the ability to access certain data about users.

## Running the Project
- Set environment variables from script, which is not included in repository
- Build project with `mvn clean install`
- Start redis-server
- Run Project in terminal with command `cd ./api &&
mvn spring-boot:run`
  - command `mvn spring-boot:run` does a build first
- OR Run Project through IntelliJ build/run tools
  
## Running Automated Tests
- First make sure to project is running
- Run classes from api_tests directory
