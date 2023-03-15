package com.jathursh.sprngboot_and_springsec_jwt.repository;

import com.jathursh.sprngboot_and_springsec_jwt.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {

    Role findByName(String name);
}
