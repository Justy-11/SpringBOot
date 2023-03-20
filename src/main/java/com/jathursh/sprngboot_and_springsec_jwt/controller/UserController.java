package com.jathursh.sprngboot_and_springsec_jwt.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jathursh.sprngboot_and_springsec_jwt.entity.Role;
import com.jathursh.sprngboot_and_springsec_jwt.entity.User;
import com.jathursh.sprngboot_and_springsec_jwt.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api")
//@RequiredArgsConstructor
public class UserController {

//    private final UserService userService;

    @Autowired
    private UserService userService;

    // The ResponseEntity.ok() method is called to create a new ResponseEntity object with an HTTP status code of 200 (OK). The body method is then called to set the response body to the result of calling the getUsers method of the userService.
    //The userService is an instance of a service class that provides methods for interacting with a database or other data source. The getUsers method of the userService returns a list of users.
    //By returning a ResponseEntity object with the list of users as the response body, this method is designed to be used in a web application to handle an HTTP request for a list of users. The ResponseEntity object provides a convenient way to set the HTTP status code, headers, and response body in a single object.

    //@ApiImplicitParams annotation is used to define the header parameter

    @Operation(summary = "This API will fetch all the users stored in DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Fetched all users from DB",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404",
                    description = "Not available",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Access Denied",
                    content = @Content)
    })  // there can be multiple responses, Above annotations for swagger-ui
    @GetMapping("/users")
    @SecurityRequirement(name = "bearer-token")  // -- added on 14/3/2023 --
    public ResponseEntity<List<User>>getUsers(){
        return ResponseEntity.ok().body(userService.getUsers());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Operation(summary = "This API will save a user in DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Saved the user in DB",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Saving user unsuccessful",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Access Denied",
                    content = @Content)
    })
    @PostMapping("/user/save")
    @SecurityRequirement(name = "bearer-token")  // -- added on 14/3/2023 --
    public ResponseEntity<User>saveUser(@RequestBody User user){
        return ResponseEntity.ok().body(userService.saveUser(user));
    }
    // URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
    // TODO: error when testing it, current context path is not found

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Operation(summary = "This API will save the role of user in DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Saved the role of user in DB",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Saving role unsuccessful",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Access Denied",
                    content = @Content)
    })
    @PostMapping("/role/save")
    @SecurityRequirement(name = "bearer-token")  // -- added on 14/3/2023 --
    public ResponseEntity<Role>saveRole(@RequestBody Role role){
        return ResponseEntity.ok().body(userService.saveRole(role));
    }

    // URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());

    ///////////////////////////////////////////////////////////////////////////////////////////////////////// TODO
    @Operation(summary = "This API will give a role access to another user in DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Adding given role to the given user",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Not available",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Access Denied",
                    content = @Content)
    })
    @PostMapping("/role/addtouser")
    @SecurityRequirement(name = "bearer-token")  // -- added on 14/3/2023 --
    public ResponseEntity<?>addRoleToUser(@RequestBody RoleToUserForm roleToUserForm){
        userService.addRoleToUser(roleToUserForm.getUsername(), roleToUserForm.getRoleName());
        return ResponseEntity.ok().build();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////// TODO
    @Operation(summary = "This API will generate a new access token (used when an access token expires) from given refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Generated new access token",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Not available",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Access Denied",
                    content = @Content)
    })
    @GetMapping("/token/refresh")
    @SecurityRequirement(name = "refresh-token")  // -- added on 14/3/2023 --
    public void refreshToken(HttpServletRequest request, HttpServletResponse  response) throws IOException {

        // ** using the access token as refresh token to generate new access token **
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());  // use same secret as in the CustomAuthenticationFilter
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                User user = userService.getUser(username);

                String access_token  = JWT.create().withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))  // 10 * 60 * 1000 - 10 minutes expiration time
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            }catch(Exception exception){

                response.addHeader("Error ",exception.getMessage());
                //response.setStatus(FORBIDDEN.value());

                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }

        }else{
            throw new RuntimeException("Refresh token is missing");
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*@Operation(summary = "This API used for testing purposes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Said Hello",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Not available",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Access Denied",
                    content = @Content)
    })
    // Test -- 12/3/2023 --
    @GetMapping("/hello")
    public String sayHello(){
        return "Hello Testing ....";
    }*/
}

@Data
class RoleToUserForm {
    private String username;
    private String roleName;
}
