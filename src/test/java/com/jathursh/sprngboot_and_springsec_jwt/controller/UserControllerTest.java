package com.jathursh.sprngboot_and_springsec_jwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jathursh.sprngboot_and_springsec_jwt.entity.Role;
import com.jathursh.sprngboot_and_springsec_jwt.entity.User;
import com.jathursh.sprngboot_and_springsec_jwt.service.UserService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    User USER1 = new User(null, "John Abraham", "John","1234",new ArrayList<>());
    User USER2 = new User(null, "James Bean", "James","1234",new ArrayList<>());
    User USER3 = new User(null, "Van Anderson", "Van","1234",new ArrayList<>());

    // added on 19/3/2023
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Captor
    private ArgumentCaptor<Object> captor;

    @Test
    void getUsers() {

        List<User> users = new ArrayList<>(Arrays.asList(USER1, USER2, USER3));

        // when userService.getUsers() is called then return list of users created, rather than going to DB
        Mockito.when(userService.getUsers()).thenReturn(users);

        ResponseEntity<List<User>> usersResponseEntity = userController.getUsers();
        //System.out.println(usersResponseEntity);
        assertNotNull(usersResponseEntity);
        assertEquals(HttpStatus.OK, usersResponseEntity.getStatusCode());
    }

    @Test
    void saveUser() {
        User user = new User();
        user.setId(null);
        user.setUsername("Ada");
        user.setName("Ada yam");
        user.setPassword("1234");

        // Stubbing
        Mockito.when(userService.saveUser(user)).thenReturn(user);
        // OR we can do like below,
        // Mockito.doReturn(user).when(userService).saveUser(user);

        ResponseEntity<User> userResponseEntity = userController.saveUser(user);

        // verify that the response body is the same as the mock User object
        assertEquals(user, userResponseEntity.getBody());
        assertNotNull(userResponseEntity);

        /*
        // verify that the response status is 201 Created TODO - 201 and 200 not same
        URI expectedUri = UriComponentsBuilder.fromPath("/api/user/save").build().toUri();
        ResponseEntity<User> expectedResponse = ResponseEntity.created(expectedUri).body(user);
        assertEquals(HttpStatus.CREATED, userResponseEntity.getStatusCode());
        assertEquals(expectedResponse, userResponseEntity);*/
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

        // TODO: For void userService methods, there is a different method of stubbing (but its showing an error)
        // Mockito.doNothing().when(userService).addRoleToUser("TestUser", "ROLE_USER");

        // verify that the response entity has status code 200 OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void refreshToken() throws IOException {

        //added in 19/3/2023
        // Set up mock objects
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbjEiLCJleHAiOjE2NDc2ODA0NTQsImlhdCI6MTY0NzY3NzY1NCwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwIiwicm9sZXMiOlsiUk9MRV9BRE1JTiIsIlJPTEVfVVNFUiJdfQ.9crXcFOQrAgQRf7YwGjy6r8h1pNXGTsV7v4SlZ4-xV8");

        User mockUser = new User(null,"Jathu John","Jathu", "1234", new ArrayList<>());
        Mockito.when(userService.getUser("Jathu")).thenReturn(mockUser);

        Mockito.when(response.getOutputStream()).thenReturn(Mockito.mock(ServletOutputStream.class));

        // Call the controller method
        userController.refreshToken(request, response);

        // Verify the response
        Mockito.verify(response).setContentType("application/json");
        Mockito.verify(response, Mockito.atLeastOnce()).getOutputStream();

        if(captor.getValue() != null){
            Mockito.verify(response.getOutputStream(), times(2)).write(captor.capture().toString().getBytes());
        }


        // Decode the response JSON
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> actualResponse = mapper.readValue(captor.getValue().toString(), Map.class);

        // Assert the response
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.get("access_token"));
        assertNotNull(actualResponse.get("refresh_token"));
        assertTrue(!actualResponse.get("access_token").isEmpty());
        assertTrue(!actualResponse.get("refresh_token").isEmpty());
//        assertNotNull(actualResponse.get("access_token"));
//        assertNotNull(actualResponse.get("refresh_token"));



    }

}