package com.example.beathelper.repositories;

import com.example.beathelper.entities.Key;
import com.example.beathelper.enums.KeyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeyRepository extends JpaRepository<Key, Long> {
    Optional<Key> findByKey(KeyType key);
}
