package com.example.beathelper.entities;

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
//    @Transient
//    private String confirmPassword;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public String getConfirmPassword() {
//        return confirmPassword;
//    }
//
//    public void setConfirmPassword(String confirmPassword) {
//        this.confirmPassword = confirmPassword;
//    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public List<BPM> getBpms() {
        return bpms;
    }

    public void setBpms(List<BPM> bpms) {
        this.bpms = bpms;
    }

    public List<Key> getKeys() {
        return keys;
    }

    public void setKeys(List<Key> keys) {
        this.keys = keys;
    }
}

