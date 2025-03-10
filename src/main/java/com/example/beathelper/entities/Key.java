package com.example.beathelper.entities;

import com.example.beathelper.enums.KeyType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "keys")
public class Key {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private KeyType name;

    @Column(nullable = true)
    @Size(max = 3)
    @ElementCollection
    private List<KeyType> relatedKeys;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public KeyType getName() {
        return name;
    }

    public void setName(KeyType name) {
        this.name = name;
    }

    public List<KeyType> getRelatedKeys() {
        return relatedKeys;
    }

    public void setRelatedKeys(List<KeyType> relatedKeys) {
        this.relatedKeys = relatedKeys;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}
