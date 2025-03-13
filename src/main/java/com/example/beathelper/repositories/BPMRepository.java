package com.example.beathelper.repositories;

import com.example.beathelper.entities.BPM;
import com.example.beathelper.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BPMRepository extends JpaRepository<BPM, Long> {
    Optional<BPM> findByBPM(Integer bpmValue);
    List<BPM> findByUser(User user);
}
