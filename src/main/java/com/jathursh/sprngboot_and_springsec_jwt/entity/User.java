package com.jathursh.sprngboot_and_springsec_jwt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.AUTO;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;
    private String name;
    private String username;
    private String password;

    //@ManyToMany(fetch = EAGER)  //commented on 15/3/2023
    //@ManyToMany(cascade = CascadeType.PERSIST)
    @ManyToMany(cascade = CascadeType.ALL)
    private Collection<Role> roles = new ArrayList<>();



}
