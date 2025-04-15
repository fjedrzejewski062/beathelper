package com.example.beathelper.repositories;

import com.example.beathelper.entities.BPM;
import com.example.beathelper.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface BPMRepository extends JpaRepository<BPM, Long>, JpaSpecificationExecutor<BPM> {
    Optional<BPM> findByBpmValue(Integer bpmValue);
    List<BPM> findByCreatedBy(User createdBy);
    Page<BPM> findByCreatedBy(User createdBy, Pageable pageable);
}
