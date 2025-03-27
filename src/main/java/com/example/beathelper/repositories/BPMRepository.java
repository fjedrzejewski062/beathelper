package com.example.beathelper.repositories;

import com.example.beathelper.entities.BPM;
import com.example.beathelper.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BPMRepository extends JpaRepository<BPM, Long> {
    Optional<BPM> findByBpmValue(Integer bpmValue);
    List<BPM> findByCreatedBy(User createdBy);
    Page<BPM> findByCreatedBy(User createdBy, Pageable pageable);
    Page<BPM> findByCreatedByAndBpmValueBetween(User createdBy, Integer min, Integer max, Pageable pageable);
    Page<BPM> findByCreatedByAndCreatedAtBetween(User createdBy, LocalDateTime start, LocalDateTime end, Pageable pageable);


    // Wyszukiwanie po BPM
    @Query("SELECT b FROM BPM b WHERE b.createdBy = :createdBy AND b.bpmValue = :bpmValue")
    Page<BPM> searchByBpm(User createdBy, Integer bpmValue, Pageable pageable);

    // Wyszukiwanie po BPM greater than or equal to min
    Page<BPM> findByCreatedByAndBpmValueGreaterThanEqual(User createdBy, Integer bpmValue, Pageable pageable);

    // Wyszukiwanie po BPM less than or equal to max
    Page<BPM> findByCreatedByAndBpmValueLessThanEqual(User createdBy, Integer bpmValue, Pageable pageable);
}
