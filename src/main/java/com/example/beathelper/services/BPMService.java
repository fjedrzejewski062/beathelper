package com.example.beathelper.services;

import com.example.beathelper.entities.BPM;
import com.example.beathelper.entities.User;
import com.example.beathelper.repositories.BPMRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class BPMService {
    private final BPMRepository bpmRepository;
    private final Random ran = new Random();

    public BPMService(BPMRepository bpmRepository) {
        this.bpmRepository = bpmRepository;
    }

    public BPM randomBPM(User createdBy, Integer min, Integer max){
        if(min == null || max == null || min >= max){
            throw new IllegalArgumentException("The provided BPM range is invalid.");
        }

        if (min < 1 || max > 250) {
            throw new IllegalArgumentException("BPM must be between 1 and 250.");
        }

        int randomBPMValue = generateRandomBPMValue(min, max);

        BPM bpm = new BPM();
        bpm.setBpmValue(randomBPMValue);
        bpm.setCreatedBy(createdBy);

        return bpmRepository.save(bpm);
    }
    public int generateRandomBPMValue(int min, int max) {
        return ran.nextInt(min, max);
    }
    public List<BPM> findBPMsByUser(User createdBy){
        return bpmRepository.findByCreatedBy(createdBy);
    }

    public BPM findById(Long id){
        return bpmRepository.findById(id).orElse(null);
    }

    public BPM updateBPM(BPM bpm){
        return bpmRepository.save(bpm);
    }

    public void deleteBPM(Long id){
        bpmRepository.deleteById(id);
    }

    public Optional<BPM> findByBPMValue(Integer bpmValue){
        return bpmRepository.findByBpmValue(bpmValue);
    }


    public Page<BPM> findBPMsByUser(User createdBy, int page) {
        Pageable pageable = PageRequest.of(page, 10); // 10 rekordów na stronę
        return bpmRepository.findByCreatedBy(createdBy, pageable);
    }

    public Page<BPM> findFilteredBPMs(User createdBy, Integer min, Integer max, Integer bpmValue, String startDate, String endDate, Pageable pageable) {
        // Tworzymy specyfikację
        Specification<BPM> spec = Specification.where(null);
        // Filtracja po wartości BPM (jeśli podano)
        if (bpmValue != null) {
            spec = spec.and((root, query, builder) -> builder.equal(root.get("bpmValue"), bpmValue));
        }

        // Filtracja po zakresie BPM (min i max)
        if (min != null && max != null) {
            spec = spec.and((root, query, builder) -> builder.between(root.get("bpmValue"), min, max));
        } else if (min != null) {
            spec = spec.and((root, query, builder) -> builder.greaterThanOrEqualTo(root.get("bpmValue"), min));
        } else if (max != null) {
            spec = spec.and((root, query, builder) -> builder.lessThanOrEqualTo(root.get("bpmValue"), max));
        }

        // Filtracja po dacie utworzenia (jeśli podano)
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            spec = spec.and((root, query, builder) -> builder.between(root.get("createdAt"), start, end));
        }

        // Wykonanie zapytania z dynamiczną specyfikacją
        return bpmRepository.findAll(spec, pageable);
    }
}
