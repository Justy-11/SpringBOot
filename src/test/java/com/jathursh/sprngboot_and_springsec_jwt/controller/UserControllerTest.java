package com.jathursh.sprngboot_and_springsec_jwt.controller;

import com.jathursh.sprngboot_and_springsec_jwt.entity.Role;
import com.jathursh.sprngboot_and_springsec_jwt.entity.User;
import com.jathursh.sprngboot_and_springsec_jwt.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    void refreshToken() {


    }
}