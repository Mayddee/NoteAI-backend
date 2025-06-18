package org.example.ainote.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name="email")
    private String email;

    @Column(name = "username")
    private String username;


    @Column(name = "password")
    private String password;


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "users_roles")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(inverseJoinColumns = @JoinColumn(name = "note_id"))
    private List<Note> notes;

}
