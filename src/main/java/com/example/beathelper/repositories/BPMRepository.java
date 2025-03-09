package com.example.beathelper.repositories;

import com.example.beathelper.entity.BPM;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BPMRepository extends JpaRepository<BPM, Long> {
}
