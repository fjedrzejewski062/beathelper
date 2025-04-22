package com.example.beathelper.service;

import com.example.beathelper.entities.BPM;
import com.example.beathelper.entities.User;
import com.example.beathelper.repositories.BPMRepository;
import com.example.beathelper.repositories.UserRepository;
import com.example.beathelper.services.BPMService;
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
public class BPMServiceTest {

    @Autowired
    private BPMService bpmService;

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
    @DisplayName("Should generate and save BPM")
    public void testGenerateAndSaveBPM() {
        User user = createValidUser();

        BPM generatedBPM = bpmService.randomBPM(user, 60, 120);

        assertThat(generatedBPM.getBpmValue()).isBetween(60, 120);
        assertThat(generatedBPM.getCreatedBy()).isEqualTo(user);

        Optional<BPM> savedBPM = bpmRepository.findById(generatedBPM.getId());
        assertThat(savedBPM).isPresent();
        assertThat(savedBPM.get().getBpmValue()).isEqualTo(generatedBPM.getBpmValue());
    }

    @Test
    @DisplayName("Should find BPM by ID")
    public void testFindById() {
        User user = createValidUser();

        BPM bpm = new BPM();
        bpm.setBpmValue(120);
        bpm.setCreatedBy(user);

        bpmRepository.save(bpm);

        BPM foundBPM = bpmService.findById(bpm.getId());

        assertThat(foundBPM).isNotNull();
        assertThat(foundBPM.getBpmValue()).isEqualTo(120);
    }

    @Test
    @DisplayName("Should update BPM")
    public void testUpdateBPM() {
        User user = createValidUser();

        BPM bpm = new BPM();
        bpm.setBpmValue(100);
        bpm.setCreatedBy(user);

        bpmRepository.save(bpm);

        bpm.setBpmValue(110);

        BPM updatedBPM = bpmService.updateBPM(bpm);

        assertThat(updatedBPM.getBpmValue()).isEqualTo(110);

        Optional<BPM> fetchedBPM = bpmRepository.findById(bpm.getId());
        assertThat(fetchedBPM).isPresent();
        assertThat(fetchedBPM.get().getBpmValue()).isEqualTo(110);
    }

    @Test
    @DisplayName("Should delete BPM")
    public void testDeleteBPM() {
        User user = createValidUser();

        BPM bpm = new BPM();
        bpm.setBpmValue(130);
        bpm.setCreatedBy(user);

        bpmRepository.save(bpm);

        bpmService.deleteBPM(bpm.getId());

        Optional<BPM> deletedBPM = bpmRepository.findById(bpm.getId());
        assertThat(deletedBPM).isEmpty();
    }

    @Test
    @DisplayName("Should find BPM by value")
    public void testFindByBPMValue() {
        User user = createValidUser();

        BPM bpm = new BPM();
        bpm.setBpmValue(125);
        bpm.setCreatedBy(user);

        bpmRepository.save(bpm);

        Optional<BPM> foundBPM = bpmService.findByBPMValue(125);

        assertThat(foundBPM).isPresent();
        assertThat(foundBPM.get().getBpmValue()).isEqualTo(125);
    }

    @Test
    @DisplayName("Should filter BPMs by value")
    public void testFindFilteredBPMs() {
        User user = createValidUser();

        BPM bpm1 = new BPM();
        bpm1.setBpmValue(100);
        bpm1.setCreatedBy(user);
        bpmRepository.save(bpm1);

        BPM bpm2 = new BPM();
        bpm2.setBpmValue(120);
        bpm2.setCreatedBy(user);
        bpmRepository.save(bpm2);

        Page<BPM> filteredBPMs = bpmService.findFilteredBPMs(user, 90, 110, null, null, null, PageRequest.of(0, 10));

        assertThat(filteredBPMs.getContent()).hasSize(1);
        assertThat(filteredBPMs.getContent().get(0).getBpmValue()).isEqualTo(100);
    }
}
