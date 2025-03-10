package com.example.beathelper.services;

import com.example.beathelper.entities.BPM;
import com.example.beathelper.entities.User;
import com.example.beathelper.repositories.BPMRepository;
import org.springframework.stereotype.Service;

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
        if(min != null || max != null || min >= max){
            throw new IllegalArgumentException("Podany zakres BPM jest niepoprawny");
        }

        int randomBPMValue = ran.nextInt(min, max);

        BPM bpm = new BPM();
        bpm.setBpmValue(randomBPMValue);
        bpm.setCreatedBy(createdBy);

        return bpmRepository.save(bpm);
    }

    public Optional<BPM> findById(Long id){
        return bpmRepository.findById(id);
    }

    public void deleteBPM(BPM bpm){
        bpmRepository.delete(bpm);
    }

    public Optional<BPM> findByBPM(Integer bpmValue){
        return bpmRepository.findByBPM(bpmValue);
    }
}
