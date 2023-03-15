package com.jathursh.sprngboot_and_springsec_jwt.repository;

import com.jathursh.sprngboot_and_springsec_jwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
