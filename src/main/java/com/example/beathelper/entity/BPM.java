package com.example.beathelper.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;

@Entity
@Table(name = "bpms")
public class BPM {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Integer bpmValue;

    private Integer minValue;
    private Integer maxValue;
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @AssertTrue(message = "minValue musi być mniejsze niż maxValue")
    public boolean isBPMValid() {
        if (bpmValue == null || minValue == null || maxValue == null) {
            return false;
        }

        if (minValue >= maxValue) {
            return false;
        }

        if (bpmValue < minValue || bpmValue > maxValue) {
            return false;
        }

        return true;
    }
}
