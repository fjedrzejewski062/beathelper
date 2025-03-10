package com.example.beathelper.repositories;

import com.example.beathelper.entities.BPM;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BPMRepository extends JpaRepository<BPM, Long> {
    Optional<BPM> findByBPM(Integer bpmValue);
}
