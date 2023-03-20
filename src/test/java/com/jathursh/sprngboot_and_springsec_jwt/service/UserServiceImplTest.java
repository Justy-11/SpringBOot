package com.jathursh.sprngboot_and_springsec_jwt.service;

import com.jathursh.sprngboot_and_springsec_jwt.entity.Role;
import com.jathursh.sprngboot_and_springsec_jwt.entity.User;
import com.jathursh.sprngboot_and_springsec_jwt.repository.RoleRepo;
import com.jathursh.sprngboot_and_springsec_jwt.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    // added on 19/3/2023
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleRepo roleRepo;

    User USER1 = new User(null, "John Abraham", "John","1234",new ArrayList<>());
    User USER2 = new User(null, "James Bean", "James","1234",new ArrayList<>());
    User USER3 = new User(null, "Van Anderson", "Van","1234",new ArrayList<>());

    @Test
    void loadUserByUsername() throws UsernameNotFoundException {
        /*User user = new User();
        user.setId(null);
        user.setUsername("username");
        user.setPassword("1234");
        user.setName("Full name");

        Mockito.when(userRepo.findByUsername("username")).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername("username");

        assertNotNull(userDetails);
        assertEquals("username", userDetails.getUsername());*/

        // Create a mock user object
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        //user.setRoles(Collections.singletonList(new Role("ROLE_USER")));

        // Define the behavior of the repository mock
        Mockito.when(userRepo.findByUsername("username")).thenReturn(user);

        // Call the method under test
        UserDetails userDetails = userService.loadUserByUsername("username");

        // Verify that the user was retrieved from the repository
        assertNotNull(userDetails);
        assertEquals("username", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());

        // Verify that the roles were mapped to authorities
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(0, authorities.size());
        assertNotNull(user);
    }

    @Test
    void saveUser() {
        User user = new User();
        user.setName("John");
        user.setPassword("password");
        user.setId(null);

        PasswordEncoder passwordEncoderMock = Mockito.mock(PasswordEncoder.class);
        //userService.setPass(passwordEncoderMock);

        //Mockito.when(passwordEncoderMock.encode(user.getPassword())).thenReturn("encodedPassword");
        Mockito.when(userRepo.save(user)).thenReturn(user);

        User savedUser = userService.saveUser(user);

        //Mockito.verify(passwordEncoderMock).encode(user.getPassword());
        Mockito.verify(userRepo).save(user);

        assertEquals(user.getName(), savedUser.getName());
        //assertEquals("encodedPassword", savedUser.getPassword());

    }

    @Test
    void saveRole() {

        Role role = new Role();
        role.setName("ROLE_ADMIN");
        role.setId(null);

        Mockito.when(roleRepo.save(role)).thenReturn(role);

        Role savedRole = userService.saveRole(role);

        assertEquals(savedRole.getName(), role.getName());
        assertNotNull(savedRole);
    }

    @Test
    void addRoleToUser() {

        String username = "username";
        String roleName = "ROLE_ADMIN";
        User user = new User();
        user.setUsername(username);
        user.setName("Full Name");
        user.setPassword("password");
        Role role = new Role();
        role.setName(roleName);

        Mockito.when(userRepo.findByUsername(username)).thenReturn(user);
        Mockito.when(roleRepo.findByName(roleName)).thenReturn(role);

        userService.addRoleToUser(username, roleName);

        assertTrue(user.getRoles().contains(role));
        //Mockito.verify(userRepo, times(1)).findByUsername(username);
        //Mockito.verify(roleRepo, times(1)).findByName(roleName);

    }

    @Test
    void getUser() {

        String username = "username";
        User user = new User();
        user.setUsername(username);
        user.setName("Full Name");
        user.setPassword("password");

        Mockito.when(userRepo.findByUsername(username)).thenReturn(user);

        User getUser = userService.getUser(username);

        assertEquals(getUser.getUsername(), user.getUsername());
    }

    @Test
    void getUsers() {

        List<User> userList = new ArrayList<>(Arrays.asList(USER1, USER2, USER3));

        Mockito.when(userRepo.findAll()).thenReturn(userList);

        List<User> resultUsers = userService.getUsers();

        assertEquals(userList, resultUsers);
        Mockito.verify(userRepo, times(1)).findAll();
    }
}