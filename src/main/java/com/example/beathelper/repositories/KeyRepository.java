package com.example.beathelper.repositories;

import com.example.beathelper.entities.Key;
import com.example.beathelper.entities.User;
import com.example.beathelper.enums.KeyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface KeyRepository extends JpaRepository<Key, Long> {
    Optional<Key> findByName(KeyType name);
    List<Key> findByCreatedBy(User createdBy);
    Page<Key> findByCreatedBy(User createdBy, Pageable pageable);
    Page<Key> findByCreatedByAndCreatedAtBetween(User createdBy, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT k FROM Key k WHERE k.createdBy = :createdBy AND k.name = :keyName")
    Page<Key> searchByKey(@Param("createdBy") User createdBy, @Param("keyName") KeyType keyName, Pageable pageable);
}
