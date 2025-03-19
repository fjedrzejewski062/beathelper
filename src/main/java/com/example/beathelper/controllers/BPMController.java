package com.example.beathelper.controllers;

import com.example.beathelper.entities.BPM;
import com.example.beathelper.entities.User;
import com.example.beathelper.services.BPMService;
import com.example.beathelper.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@Controller
public class BPMController {
    private final BPMService bpmService;
    private final UserService userService;

    public BPMController(BPMService bpmService, UserService userService) {
        this.bpmService = bpmService;
        this.userService = userService;
    }

    @GetMapping("/mybpms")
    public String myBPMs(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser){
        String email = currentUser.getUsername();
        User user = userService.findByEmail(email).orElse(null);
        model.addAttribute("user", user);
        return "myBPMs";
    }

    @GetMapping("/mybpms/random")
    public String showRandomBPM(Model model){
        model.addAttribute("bpm", new BPM());
        return "randomBPM";
    }

    @PostMapping("/mybpms/random")
    public String randomBPM(BindingResult result,
                            @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser,
                            Model model,
                            @RequestParam(value = "min", required = false) Integer min,
                            @RequestParam(value = "max", required = false) Integer max){
        if(result.hasErrors()){
            model.addAttribute("error", "Podany zakres BPM jest niepoprawny");
            return "randomBPM";
        }

        String email = currentUser.getUsername();
        User user = userService.findByEmail(email).orElse(null);

        bpmService.randomBPM(user, min, max);
        return "redirect:/mybpms";
    }

    @GetMapping("/mybpms/edit/{id}")
    public String showEditBPM(@PathVariable Long id, Model model,
                              @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser){
        BPM bpm = bpmService.findById(id);

        if(bpm == null || !bpm.getCreatedBy().getEmail().equals(currentUser.getUsername())){
            return "redirect:/mybpms";
        }

        model.addAttribute("bpm", bpm);
        return "editBPM";
    }

    @PostMapping("/mybpms/edit/{id}")
    public String editBPM(@PathVariable Long id,
                          BindingResult result,
                          @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser,
                          Model model,
                          @RequestParam(value = "min", required = false) Integer min,
                          @RequestParam(value = "max", required = false) Integer max){
        BPM existingBPM = bpmService.findById(id);

        if(existingBPM == null || !existingBPM.getCreatedBy().getEmail().equals(currentUser.getUsername())){
            return "redirect:/mybpms";
        }

        if(result.hasErrors()){
            model.addAttribute("bpm", existingBPM);
            return "editBPM";
        }
        if(min == null || max == null || min >= max){
            model.addAttribute("error", "Podany zakres BPM jest niepoprawny");
            return "editBPM";
        }

        existingBPM.setBpmValue(bpmService.generateRandomBPMValue(min, max));
        bpmService.updateBPM(existingBPM);
        return "redirect:/mybpms";
    }

    @GetMapping("/mybpms/delete/{id}")
    public String deleteBPM(@PathVariable Long id,
                            @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser){
        BPM bpm = bpmService.findById(id);
        if(bpm != null && bpm.getCreatedBy().getEmail().equals(currentUser.getUsername())){
            bpmService.deleteBPM(id);
        }
        return "redirect:/mybpms";

    }
}
