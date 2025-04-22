package com.example.beathelper.service;

import com.example.beathelper.entities.User;
import com.example.beathelper.enums.UserType;
import com.example.beathelper.repositories.UserRepository;
import com.example.beathelper.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Should register a new user")
    public void testRegisterUser() {
        User user = new User();
        user.setUsername("newUser");
        user.setEmail("newUser@example.com");
        user.setPassword("plainPassword");

        User registeredUser = userService.register(user);

        assertThat(registeredUser.getId()).isNotNull();
        assertThat(registeredUser.getPassword()).isNotEqualTo("plainPassword");
        assertThat(passwordEncoder.matches("plainPassword", registeredUser.getPassword())).isTrue();
        assertThat(registeredUser.getRegistrationDate()).isNotNull();
        assertThat(registeredUser.isBanned()).isFalse();
        assertThat(registeredUser.isDeleted()).isFalse();

        Optional<User> fetched = userRepository.findByEmail("newUser@example.com");
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getUsername()).isEqualTo("newUser");
    }

    @Test
    @DisplayName("Should find user by email")
    public void testFindByEmail() {
        User user = new User();
        user.setUsername("emailUser");
        user.setEmail("emailUser@example.com");
        user.setPassword("password123");

        userService.register(user);

        Optional<User> foundUser = userService.findByEmail("emailUser@example.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("emailUser");
    }

    @Test
    @DisplayName("Should find user by username")
    public void testFindByUsername() {
        User user = new User();
        user.setUsername("uniqueUser");
        user.setEmail("uniqueUser@example.com");
        user.setPassword("password123");

        userService.register(user);

        Optional<User> foundUser = userService.findByUsername("uniqueUser");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("uniqueUser@example.com");
    }

    @Test
    @DisplayName("Should update user data")
    public void testUpdateUser() {
        User user = new User();
        user.setUsername("updateUser");
        user.setEmail("updateUser@example.com");
        user.setPassword("password123");

        User registeredUser = userService.register(user);

        registeredUser.setUsername("updatedUsername");
        registeredUser.setEmail("updatedEmail@example.com");

        User updatedUser = userService.update(registeredUser);

        Optional<User> fetched = userRepository.findById(updatedUser.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getUsername()).isEqualTo("updatedUsername");
        assertThat(fetched.get().getEmail()).isEqualTo("updatedEmail@example.com");
    }

    @Test
    @DisplayName("Should soft delete user")
    public void testSoftDeleteUser() {
        User user = new User();
        user.setUsername("deleteUser");
        user.setEmail("deleteUser@example.com");
        user.setPassword("password123");

        User registeredUser = userService.register(user);

        userService.softDelete(registeredUser);

        User fetched = userService.findById(registeredUser.getId());
        assertThat(fetched).isNotNull();
        assertThat(fetched.isDeleted()).isTrue();
        assertThat(fetched.getUsername()).startsWith("DELETED-USER-");
        assertThat(fetched.getEmail()).startsWith("DELETED-USER-");
        assertThat(passwordEncoder.matches("DELETED-PASSWORD", fetched.getPassword())).isTrue();
    }

    @Test
    @DisplayName("Should find user by ID")
    public void testFindById() {
        User user = new User();
        user.setUsername("findUser");
        user.setEmail("findUser@example.com");
        user.setPassword("password123");

        User registeredUser = userService.register(user);

        User fetched = userService.findById(registeredUser.getId());

        assertThat(fetched).isNotNull();
        assertThat(fetched.getUsername()).isEqualTo("findUser");
        assertThat(fetched.getEmail()).isEqualTo("findUser@example.com");
    }

    @Test
    @DisplayName("Should filter users by various criteria (UserType, role, banned, deleted, etc.)")
    public void testFindFilteredUsers() {
        User user1 = new User();
        user1.setUsername("filterUser1");
        user1.setEmail("filter1@example.com");
        user1.setPassword("password123");
        user1.setRole("USER");
        user1.setBanned(false);
        user1.setDeleted(false);
        user1.setRegistrationDate(LocalDateTime.of(2024, 1, 15, 10, 0));
        user1.setLastLogin(LocalDateTime.of(2024, 2, 1, 12, 0));
        user1.setUserType(UserType.ARTIST);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("filterUser2");
        user2.setEmail("filter2@example.com");
        user2.setPassword("password123");
        user2.setRole("ADMIN");
        user2.setBanned(true);
        user2.setDeleted(false);
        user2.setRegistrationDate(LocalDateTime.of(2024, 3, 10, 14, 0));
        user2.setLastLogin(LocalDateTime.of(2024, 3, 11, 9, 0));
        user2.setUserType(UserType.ARTIST);
        userRepository.save(user2);

        Pageable pageable = Pageable.ofSize(10);

        Page<User> results = userService.findFilteredUsers(
                "filterUser",
                "filter",
                UserType.ARTIST,
                "ADMIN",
                true,
                false,
                "2024-03-01",
                "2024-03-31",
                "2024-03-01",
                "2024-03-31",
                pageable
        );

        assertThat(results).isNotNull();
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getUsername()).isEqualTo("filterUser2");
    }

}
