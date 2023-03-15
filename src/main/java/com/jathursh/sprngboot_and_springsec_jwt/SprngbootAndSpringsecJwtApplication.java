package com.jathursh.sprngboot_and_springsec_jwt;

import com.jathursh.sprngboot_and_springsec_jwt.entity.Role;
import com.jathursh.sprngboot_and_springsec_jwt.entity.User;
import com.jathursh.sprngboot_and_springsec_jwt.service.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class SprngbootAndSpringsecJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(SprngbootAndSpringsecJwtApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(UserService userService){
		return args ->{
			userService.saveRole(new Role(null,"ROLE_USER"));
			userService.saveRole(new Role(null,"ROLE_MANAGER"));
			userService.saveRole(new Role(null,"ROLE_ADMIN"));
			userService.saveRole(new Role(null,"ROLE_SUPER_ADMIN"));

			userService.saveUser(new User(null, "Jathurshan Pathmarasa", "Jathursh","1234",new ArrayList<>()));
			userService.saveUser(new User(null, "John Rohan", "John","1234",new ArrayList<>()));
			userService.saveUser(new User(null, "Jim Robert", "Jim","1234",new ArrayList<>()));
			userService.saveUser(new User(null, "Cherry Caman", "Cherry","1234",new ArrayList<>()));

			userService.addRoleToUser("Jathursh","ROLE_USER");
			userService.addRoleToUser("Jathursh","ROLE_MANAGER");
			userService.addRoleToUser("John","ROLE_MANAGER");
			userService.addRoleToUser("Jim","ROLE_ADMIN");
			userService.addRoleToUser("Cherry","ROLE_SUPER_ADMIN");
			userService.addRoleToUser("Cherry","ROLE_ADMIN");
			userService.addRoleToUser("Cherry","ROLE_USER");

		};

	}

	@Bean
	PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
}
