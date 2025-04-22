package com.example.beathelper.repository;

import com.example.beathelper.entities.BPM;
import com.example.beathelper.entities.User;
import com.example.beathelper.repositories.BPMRepository;
import com.example.beathelper.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = "spring.datasource.initialization-mode=never")
class BPMRepositoryTest {

    @Autowired
    private BPMRepository bpmRepository;

    @Autowired
    private UserRepository userRepository;

    private User createValidUser() {
        User user = new User();
        user.setUsername("bpmuser");
        user.setEmail("bpmuser" + System.currentTimeMillis() + "@example.com");
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
    @DisplayName("Should find BPM by bpmValue")
    void testFindByBpmValue() {
        BPM bpm = new BPM();
        bpm.setBpmValue(120);
        bpmRepository.save(bpm);

        Optional<BPM> result = bpmRepository.findByBpmValue(120);

        assertTrue(result.isPresent());
        assertEquals(120, result.get().getBpmValue());
    }

    @Test
    @DisplayName("Should find BPM records by creator")
    void testFindByCreatedBy() {
        User user = createValidUser();

        BPM bpm1 = new BPM();
        bpm1.setBpmValue(100);
        bpm1.setCreatedBy(user);

        BPM bpm2 = new BPM();
        bpm2.setBpmValue(110);
        bpm2.setCreatedBy(user);

        bpmRepository.saveAll(List.of(bpm1, bpm2));

        List<BPM> result = bpmRepository.findByCreatedBy(user);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should return paged BPM results for given creator")
    void testFindByCreatedByWithPaging() {
        User user = createValidUser();

        for (int i = 0; i < 15; i++) {
            BPM bpm = new BPM();
            bpm.setBpmValue(90 + i);
            bpm.setCreatedBy(user);
            bpmRepository.save(bpm);
        }

        Pageable pageable = PageRequest.of(0, 10);
        Page<BPM> page = bpmRepository.findByCreatedBy(user, pageable);

        assertEquals(10, page.getContent().size());
        assertEquals(2, page.getTotalPages());
    }
}
