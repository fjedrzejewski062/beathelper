package com.example.beathelper.repository;

import com.example.beathelper.entities.User;
import com.example.beathelper.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@TestPropertySource(properties = "spring.datasource.initialization-mode=never")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should save user and find by ID")
    public void testSaveAndFindId() {
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("testUser@example.com");
        user.setPassword("password123");
        user.setProfileImage("/img/default_profile.png");
        user.setRole("USER");
        user.setBanned(false);
        user.setDeleted(false);
        user.setRegistrationDate(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        entityManager.flush();

        assertThat(savedUser.getId()).isNotNull();

        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testUser");
    }

    @Test
    @DisplayName("Should find user by email")
    public void testFindByEmail() {
        User user = new User();
        user.setUsername("emailUser");
        user.setEmail("emailUser@example.com");
        user.setPassword("password123");
        user.setProfileImage("/img/default_profile.png");
        user.setRole("USER");
        user.setBanned(false);
        user.setDeleted(false);
        user.setRegistrationDate(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());

        entityManager.persist(user);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findByEmail("emailUser@example.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("emailUser");
    }

    @Test
    @DisplayName("Should find user by username")
    public void testFindByUsername() {
        User user = new User();
        user.setUsername("uniqueUsername");
        user.setEmail("unique@example.com");
        user.setPassword("password123");
        user.setProfileImage("/img/default_profile.png");
        user.setRole("USER");
        user.setBanned(false);
        user.setDeleted(false);
        user.setRegistrationDate(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());

        entityManager.persist(user);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findByUsername("uniqueUsername");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("unique@example.com");
    }

    @Test
    @DisplayName("Should update user data")
    public void testUpdateUser() {
        User user = new User();
        user.setUsername("updateUser");
        user.setEmail("updateUser@example.com");
        user.setPassword("password123");
        user.setProfileImage("/img/default_profile.png");
        user.setRole("USER");
        user.setBanned(false);
        user.setDeleted(false);
        user.setRegistrationDate(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());

        User savedUser = entityManager.persistAndFlush(user);

        savedUser.setUsername("updatedUser");
        savedUser.setEmail("updatedEmail@example.com");
        savedUser.setProfileImage("/img/updated_profile.png");
        User updatedUser = userRepository.save(savedUser);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findById(updatedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("updatedUser");
        assertThat(foundUser.get().getEmail()).isEqualTo("updatedEmail@example.com");
        assertThat(foundUser.get().getProfileImage()).isEqualTo("/img/updated_profile.png");
    }

    @Test
    @DisplayName("Should delete user")
    public void testDeleteUser() {
        User user = new User();
        user.setUsername("deleteUser");
        user.setEmail("deleteUser@example.com");
        user.setPassword("password123");
        user.setProfileImage("/img/default_profile.png");
        user.setRole("USER");
        user.setBanned(false);
        user.setDeleted(false);
        user.setRegistrationDate(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());

        User savedUser = entityManager.persistAndFlush(user);

        userRepository.delete(savedUser);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("Should enforce unique constraints on username and email")
    public void testUniqueConstraints() {
        User user1 = new User();
        user1.setUsername("uniqueUser");
        user1.setEmail("uniqueUser@example.com");
        user1.setPassword("password123");
        user1.setProfileImage("/img/default_profile.png");
        user1.setRole("USER");
        user1.setBanned(false);
        user1.setDeleted(false);
        user1.setRegistrationDate(LocalDateTime.now());
        user1.setLastLogin(LocalDateTime.now());
        entityManager.persistAndFlush(user1);

        User user2 = new User();
        user2.setUsername("uniqueUser");
        user2.setEmail("another@example.com");
        user2.setPassword("password123");
        user2.setProfileImage("/img/default_profile.png");
        user2.setRole("USER");
        user2.setBanned(false);
        user2.setDeleted(false);
        user2.setRegistrationDate(LocalDateTime.now());
        user2.setLastLogin(LocalDateTime.now());

        assertThrows(Exception.class, () -> {
            userRepository.saveAndFlush(user2);
        });

        User user3 = new User();
        user3.setUsername("anotherUser");
        user3.setEmail("uniqueUser@example.com");
        user3.setPassword("password123");
        user3.setProfileImage("/img/default_profile.png");
        user3.setRole("USER");
        user3.setBanned(false);
        user3.setDeleted(false);
        user3.setRegistrationDate(LocalDateTime.now());
        user3.setLastLogin(LocalDateTime.now());

        assertThrows(Exception.class, () -> {
            userRepository.saveAndFlush(user3);
        });
    }
}
