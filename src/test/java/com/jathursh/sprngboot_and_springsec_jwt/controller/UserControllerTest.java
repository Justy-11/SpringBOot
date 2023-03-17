package com.jathursh.sprngboot_and_springsec_jwt.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jathursh.sprngboot_and_springsec_jwt.entity.Role;
import com.jathursh.sprngboot_and_springsec_jwt.entity.User;
import com.jathursh.sprngboot_and_springsec_jwt.service.UserService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    User USER1 = new User(null, "John Abraham", "John","1234",new ArrayList<>());
    User USER2 = new User(null, "James Bean", "James","1234",new ArrayList<>());
    User USER3 = new User(null, "Van Anderson", "Van","1234",new ArrayList<>());

    @Test
    void getUsers() {

        List<User> users = new ArrayList<>(Arrays.asList(USER1, USER2, USER3));
        Mockito.when(userService.getUsers()).thenReturn(users);
        ResponseEntity<List<User>> usersResponseEntity = userController.getUsers();
        assertNotNull(usersResponseEntity);
    }

    @Test
    void saveUser() {
        User user = new User();
        user.setId(null);
        user.setUsername("Ada");
        user.setName("Ada yam");
        user.setPassword("1234");

        Mockito.when(userService.saveUser(user)).thenReturn(user);
        ResponseEntity<User> userResponseEntity = userController.saveUser(user);

        // verify that the response status is 201 Created TODO - 201 and 200 not same
        //assertEquals(HttpStatus.CREATED, userResponseEntity.getStatusCode());

        // verify that the response body is the same as the mock User object
        assertEquals(user, userResponseEntity.getBody());
        assertNotNull(userResponseEntity);
    }

    @Test
    void saveRole() {
        Role role = new Role();
        role.setId(null);
        role.setName("ROLE_USER");

        Mockito.when(userService.saveRole(role)).thenReturn(role);
        ResponseEntity<Role> roleResponseEntity = userController.saveRole(role);

        assertNotNull(roleResponseEntity);
        assertEquals(role,roleResponseEntity.getBody());
    }

    @Test
    void addRoleToUser() {
        RoleToUserForm roleToUserForm = new RoleToUserForm();
        roleToUserForm.setRoleName("ROLE_USER");
        roleToUserForm.setUsername("TestUser");

        // call the controller method
        ResponseEntity<?> responseEntity = userController.addRoleToUser(roleToUserForm);

        // verify that the UserService method was called with the correct arguments
        Mockito.verify(userService).addRoleToUser("TestUser", "ROLE_USER");

        // verify that the response entity has status code 200 OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void refreshToken() throws IOException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

        String username = "user1";
        User user = new User();
        user.setUsername(username);
        user.setRoles(List.of(new Role()));
        Mockito.when(userService.getUser(username)).thenReturn(user);

        /*ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Mockito.when(response.getOutputStream()).thenReturn();*/

        ServletOutputStream outputStreamMock = Mockito.mock(ServletOutputStream.class);
        Mockito.when(response.getOutputStream()).thenReturn(outputStreamMock);

        /*String authorizationHeader = request.getHeader(AUTHORIZATION);
        String refresh_token = authorizationHeader.substring("Bearer ".length());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = Mockito.mock(DecodedJWT.class);*/

        String access_token = JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .sign(algorithm);

        //Mockito.when(verifier.verify(refresh_token)).thenReturn(decodedJWT);
        //Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + decodedJWT.getToken());

        // Act
        //refreshTokenController.refreshToken(request, response);

        // Assert
        String responseString = outputStreamMock.toString();
        Map<String, String> tokens = new ObjectMapper().readValue(responseString, Map.class);
        assertTrue(tokens.containsKey("access_token"));
        assertTrue(tokens.containsKey("refresh_token"));



    }


}