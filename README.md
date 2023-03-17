# SpringBOot

#### Role based user management system with JWT (access and refresh tokens) and Swagger-ui
##### :books: Covers all important concepts of spring boot :bangbang:

<!-- Images -->
<img src="src/main/resources/Images/admin_login_access_token.png">
<img src="src/main/resources/Images/JWT_Swagger.png">
<img src="src/main/resources/Images/JWT_Swagger_authorization_token.png">

#### Frameworks  
* Spring boot :leaves:
* Mockito :black_joker:

#### :package: Dependencies
* Spring Web
* Spring Security
* Lombok
* Spring Data Jpa
* Open API (for Swagger-ui -- API Documentation)
* MySQL Driver
* java-jwt (provided by Auth0)
* Spring Boot DevTools

#### :information_source: Extra Details 
* For every Run, DB will delete and create Tables of User and Role from scratch (contains 4 users and 4 roles - used commandline runner)
* In the project, all APIs access given to ADMIN only
* can change this under securityConfig class

#### :brain: HOW Swagger-ui API Documentation works here? :bulb:
* Shows all the REST APIs 
* Certain APIs need authorization, and access token should be given to those APIs by clicking the Authorize button
* Access and refresh tokens are created after login through url (... /api/login?username=example&password=123)