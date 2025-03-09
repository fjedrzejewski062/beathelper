package com.example.beathelper.entity;

import com.example.beathelper.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Nazwa użytkownika jest wymagana")
    @Size(min = 3, max = 50, message = "Nazwa musi mieć co najmniej 3 znaki")
    @Column(unique = true)
    private String username;

    @NotEmpty(message = "Email jest wymagany")
    @Email(message = "Niepoprawny email")
    @Column(unique = true)
    private String email;

    @NotEmpty(message = "Hasło jest wymagane")
    private String password;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    private String profileImage = "/img/default_profile.png";

    private String role = "USER";

    private boolean banned = false;

    private boolean deleted = false;

    private LocalDateTime registrationDate = LocalDateTime.now();

    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "createdBy")
    private List<BPM> bpms;

    @OneToMany(mappedBy = "createdBy")
    private List<Key> keys;
}
