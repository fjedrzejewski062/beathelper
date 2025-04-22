package com.example.beathelper.repository;

import com.example.beathelper.entities.Key;
import com.example.beathelper.entities.User;
import com.example.beathelper.enums.KeyType;
import com.example.beathelper.repositories.KeyRepository;
import com.example.beathelper.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.datasource.initialization-mode=never")
class KeyRepositoryTest {

    @Autowired
    private KeyRepository keyRepository;

    @Autowired
    private UserRepository userRepository;

    private User createUser() {
        User user = new User();
        user.setUsername("keyuser");
        user.setEmail("keyuser" + System.currentTimeMillis() + "@example.com");
        user.setPassword("securePass123");
        user.setProfileImage("/img/default_profile.png");
        user.setRole("USER");
        user.setBanned(false);
        user.setDeleted(false);
        user.setRegistrationDate(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Test
    @DisplayName("Should save and retrieve Key by ID")
    void testSaveAndFindById() {
        User user = createUser();

        Key key = new Key();
        key.setName(KeyType.C_MAJOR);
        key.setCreatedBy(user);

        Key savedKey = keyRepository.save(key);

        Optional<Key> found = keyRepository.findById(savedKey.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo(KeyType.C_MAJOR);
    }

    @Test
    @DisplayName("Should find Key by name")
    void testFindByName() {
        User user = createUser();

        Key key = new Key();
        key.setName(KeyType.A_MINOR);
        key.setCreatedBy(user);
        keyRepository.save(key);

        Optional<Key> found = keyRepository.findByName(KeyType.A_MINOR);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo(KeyType.A_MINOR);
    }

    @Test
    @DisplayName("Should find all Keys created by specific user")
    void testFindByCreatedBy() {
        User user = createUser();

        Key k1 = new Key();
        k1.setName(KeyType.D_MAJOR);
        k1.setCreatedBy(user);

        Key k2 = new Key();
        k2.setName(KeyType.G_MINOR);
        k2.setCreatedBy(user);

        keyRepository.saveAll(List.of(k1, k2));

        List<Key> keys = keyRepository.findByCreatedBy(user);
        assertThat(keys).hasSize(2);
    }

    @Test
    @DisplayName("Should return paged Keys for user")
    void testFindByCreatedByWithPagination() {
        User user = createUser();

        for (int i = 0; i < 12; i++) {
            Key key = new Key();
            key.setName(KeyType.values()[i % KeyType.values().length]);
            key.setCreatedBy(user);
            keyRepository.save(key);
        }

        Page<Key> page = keyRepository.findByCreatedBy(user, PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalPages()).isGreaterThanOrEqualTo(2);
    }
}
