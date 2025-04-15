package com.example.beathelper.repositories;

import com.example.beathelper.entities.Key;
import com.example.beathelper.entities.User;
import com.example.beathelper.enums.KeyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface KeyRepository extends JpaRepository<Key, Long>, JpaSpecificationExecutor<Key> {
    Optional<Key> findByName(KeyType name);
    List<Key> findByCreatedBy(User createdBy);
    Page<Key> findByCreatedBy(User createdBy, Pageable pageable);
}
