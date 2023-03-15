package com.jathursh.sprngboot_and_springsec_jwt.service;

import com.jathursh.sprngboot_and_springsec_jwt.entity.Role;
import com.jathursh.sprngboot_and_springsec_jwt.entity.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String username,String roleName);
    User getUser(String username);
    List<User> getUsers();
}
