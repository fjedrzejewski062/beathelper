package com.example.beathelper.service;

import com.example.beathelper.entities.User;
import com.example.beathelper.repositories.UserRepository;
import com.example.beathelper.services.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should load user by username when user exists")
    public void testLoadUserByUsername_UserExists() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encodedPassword");
        user.setRole("USER");
        user.setBanned(false);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("user@example.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("user@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
        assertThat(userDetails.getAuthorities()).isNotEmpty();
    }

    @Test
    @DisplayName("Should load admin user by username")
    public void testLoadUserByUsername_AdminUser() {
        User user = new User();
        user.setEmail("admin@example.com");
        user.setPassword("encodedPassword");
        user.setRole("ADMIN");
        user.setBanned(false);

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin@example.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("admin@example.com");

        boolean hasAdminAuthority = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
        assertThat(hasAdminAuthority).isTrue();
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    public void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistent@example.com");
        });
    }

    @Test
    @DisplayName("Should load banned user by username")
    public void testLoadUserByUsername_BannedUser() {
        User bannedUser = new User();
        bannedUser.setEmail("banned@example.com");
        bannedUser.setPassword("bannedPassword");
        bannedUser.setRole("USER");
        bannedUser.setBanned(true);

        when(userRepository.findByEmail("banned@example.com")).thenReturn(Optional.of(bannedUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("banned@example.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("banned@example.com");
    }
}
