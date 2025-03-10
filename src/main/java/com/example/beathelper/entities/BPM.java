package com.example.beathelper.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;

import java.time.LocalDateTime;

@Entity
@Table(name = "bpms")
public class BPM {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Integer bpmValue;

//    private Integer minValue;
//    private Integer maxValue;
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

//    @AssertTrue(message = "minValue musi być mniejsze niż maxValue")
//    public boolean isBPMValid() {
//        if (bpmValue == null || minValue == null || maxValue == null) {
//            return false;
//        }
//
//        if (minValue >= maxValue) {
//            return false;
//        }
//
//        if (bpmValue < minValue || bpmValue > maxValue) {
//            return false;
//        }
//
//        return true;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBpmValue() {
        return bpmValue;
    }

    public void setBpmValue(Integer bpmValue) {
        this.bpmValue = bpmValue;
    }

//    public Integer getMinValue() {
//        return minValue;
//    }
//
//    public void setMinValue(Integer minValue) {
//        this.minValue = minValue;
//    }
//
//    public Integer getMaxValue() {
//        return maxValue;
//    }
//
//    public void setMaxValue(Integer maxValue) {
//        this.maxValue = maxValue;
//    }

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
