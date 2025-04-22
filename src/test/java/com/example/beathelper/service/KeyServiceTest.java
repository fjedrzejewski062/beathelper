package com.example.beathelper.service;

import com.example.beathelper.entities.Key;
import com.example.beathelper.entities.User;
import com.example.beathelper.enums.KeyType;
import com.example.beathelper.repositories.KeyRepository;
import com.example.beathelper.repositories.UserRepository;
import com.example.beathelper.services.KeyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class KeyServiceTest {

    @Autowired
    private KeyService keyService;

    @Autowired
    private KeyRepository keyRepository;

    @Autowired
    private UserRepository userRepository;

    private User createValidUser() {
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
    @DisplayName("Should generate and save a Key")
    public void testGenerateAndSaveKey() {
        User user = createValidUser();

        Key generatedKey = keyService.randomKey(user, "both");

        assertThat(generatedKey.getName()).isNotNull();
        assertThat(generatedKey.getCreatedBy()).isEqualTo(user);
        assertThat(generatedKey.getRelatedKeys()).isNotEmpty();

        Optional<Key> savedKey = keyRepository.findById(generatedKey.getId());
        assertThat(savedKey).isPresent();
        assertThat(savedKey.get().getName()).isEqualTo(generatedKey.getName());
    }

    @Test
    @DisplayName("Should find all Keys created by the user")
    public void testFindKeysByUser() {
        User user = createValidUser();

        Key key1 = new Key();
        key1.setName(KeyType.A_MAJOR);
        key1.setCreatedBy(user);
        key1.setCreatedAt(LocalDateTime.now());
        keyRepository.save(key1);

        Key key2 = new Key();
        key2.setName(KeyType.B_MINOR);
        key2.setCreatedBy(user);
        key2.setCreatedAt(LocalDateTime.now());
        keyRepository.save(key2);

        var userKeys = keyService.findKeysByUser(user);

        assertThat(userKeys).hasSize(2);
        assertThat(userKeys).extracting(Key::getName).containsExactlyInAnyOrder(KeyType.A_MAJOR, KeyType.B_MINOR);
    }

    @Test
    @DisplayName("Should find a Key by ID")
    public void testFindById() {
        User user = createValidUser();

        Key key = new Key();
        key.setName(KeyType.C_MAJOR);
        key.setCreatedBy(user);
        key.setCreatedAt(LocalDateTime.now());

        keyRepository.save(key);

        Key foundKey = keyService.findById(key.getId());

        assertThat(foundKey).isNotNull();
        assertThat(foundKey.getName()).isEqualTo(KeyType.C_MAJOR);
    }

    @Test
    @DisplayName("Should update a Key")
    public void testUpdateKey() {
        User user = createValidUser();

        Key key = new Key();
        key.setName(KeyType.A_MINOR);
        key.setCreatedBy(user);
        key.setCreatedAt(LocalDateTime.now());

        keyRepository.save(key);

        key.setName(KeyType.D_MAJOR);
        Key updatedKey = keyService.updateKey(key);

        assertThat(updatedKey.getName()).isEqualTo(KeyType.D_MAJOR);

        Optional<Key> fetchedKey = keyRepository.findById(key.getId());
        assertThat(fetchedKey).isPresent();
        assertThat(fetchedKey.get().getName()).isEqualTo(KeyType.D_MAJOR);
    }

    @Test
    @DisplayName("Should delete a Key")
    public void testDeleteKey() {
        User user = createValidUser();

        Key key = new Key();
        key.setName(KeyType.G_SHARP_MINOR);
        key.setCreatedBy(user);
        key.setCreatedAt(LocalDateTime.now());

        keyRepository.save(key);

        keyService.deleteKey(key);

        Optional<Key> deletedKey = keyRepository.findById(key.getId());
        assertThat(deletedKey).isEmpty();
    }

    @Test
    @DisplayName("Should find a Key by name")
    public void testFindByKeyName() {
        User user = createValidUser();

        Key key = new Key();
        key.setName(KeyType.F_MINOR);
        key.setCreatedBy(user);
        key.setCreatedAt(LocalDateTime.now());

        keyRepository.save(key);

        Optional<Key> foundKey = keyService.findByKey(KeyType.F_MINOR);

        assertThat(foundKey).isPresent();
        assertThat(foundKey.get().getName()).isEqualTo(KeyType.F_MINOR);
    }

    @Test
    @DisplayName("Should filter Keys by date and name")
    public void testFindFilteredKeys() {
        User user = createValidUser();

        Key key1 = new Key();
        key1.setName(KeyType.E_MINOR);
        key1.setCreatedBy(user);
        key1.setCreatedAt(LocalDateTime.now().minusDays(2));
        keyRepository.save(key1);

        Key key2 = new Key();
        key2.setName(KeyType.F_SHARP_MINOR);
        key2.setCreatedBy(user);
        key2.setCreatedAt(LocalDateTime.now().minusDays(1));
        keyRepository.save(key2);

        String startDate = LocalDateTime.now().minusDays(3).toLocalDate().toString();
        String endDate = LocalDateTime.now().toLocalDate().toString();

        Page<Key> filteredKeys = keyService.findFilteredKeys(user, KeyType.E_MINOR, startDate, endDate, PageRequest.of(0, 10));

        assertThat(filteredKeys.getContent()).hasSize(1);
        assertThat(filteredKeys.getContent().get(0).getName()).isEqualTo(KeyType.E_MINOR);
    }
}
