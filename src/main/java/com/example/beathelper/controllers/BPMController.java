package com.example.beathelper.controllers;

import com.example.beathelper.entities.BPM;
import com.example.beathelper.entities.User;
import com.example.beathelper.services.BPMService;
import com.example.beathelper.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
    public String myBPMs(Model model,
                         @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser,
                         RedirectAttributes redirectAttributes,
                         @RequestParam(required = false) Integer min,
                         @RequestParam(required = false) Integer max,
                         @RequestParam(required = false) Integer bpmValue,
                         @RequestParam(required = false) String startDate,
                         @RequestParam(required = false) String endDate,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "id") String sortField,
                         @RequestParam(defaultValue = "asc") String sortDirection) {

        String email = currentUser.getUsername();
        User user = userService.findByEmail(email).orElse(null);

        // Walidacja filtrów BPM
        if (min != null && (min < 1 || min > 250)) {
            redirectAttributes.addFlashAttribute("error", "Min BPM must be between 1 and 250.");
            return "redirect:/mybpms"; // Używamy addFlashAttribute do przekazania komunikatu
        }
        if (max != null && (max < 1 || max > 250)) {
            redirectAttributes.addFlashAttribute("error", "Max BPM must be between 1 and 250.");
            return "redirect:/mybpms"; // Używamy addFlashAttribute do przekazania komunikatu
        }
        if (min != null && max != null && min >= max) {
            redirectAttributes.addFlashAttribute("error", "Min BPM must be less than Max BPM.");
            return "redirect:/mybpms"; // Używamy addFlashAttribute do przekazania komunikatu
        }
        if (bpmValue != null && (bpmValue < 1 || bpmValue > 250)) {
            redirectAttributes.addFlashAttribute("error", "BPM value must be between 1 and 250.");
            return "redirect:/mybpms"; // Używamy addFlashAttribute do przekazania komunikatu
        }

        if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            if (end.isBefore(start)) {
                redirectAttributes.addFlashAttribute("error", "End date cannot be earlier than start date.");
                return "redirect:/mybpms"; // Używamy addFlashAttribute do przekazania komunikatu
            }
        }

        Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, 10, sort);

        Page<BPM> bpmPage = bpmService.findFilteredBPMs(user, min, max, bpmValue, startDate, endDate, pageable);

        // Jeśli brak wyników, dodaj komunikat do modelu
//        if (bpmPage.getTotalElements() == 0) {
//            model.addAttribute("error", "No BPMs found matching the given criteria.");
//        }

        model.addAttribute("user", user);
        model.addAttribute("bpms", bpmPage.getContent());
        model.addAttribute("totalPages", bpmPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "myBPMs";
    }


    @GetMapping("/mybpms/random")
    public String showRandomBPM(Model model){
        model.addAttribute("bpm", new BPM());
        return "randomBPM";
    }

    @PostMapping("/mybpms/random")
    public String randomBPM(@AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser,
                            Model model,
                            @RequestParam(value = "min", required = false) Integer min,
                            @RequestParam(value = "max", required = false) Integer max){
        String email = currentUser.getUsername();
        User user = userService.findByEmail(email).orElse(null);

        try {
            bpmService.randomBPM(user, min, max);
            return "redirect:/mybpms"; // Sukces – przekierowanie do listy BPM
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Min BPM must be less than Max BPM."); // Przekazujemy komunikat błędu
            model.addAttribute("bpm", new BPM()); // Aby formularz nie był pusty
            return "randomBPM"; // Wracamy do formularza z błędem
        }
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
                          @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser,
                          Model model,
                          @RequestParam(value = "min", required = false) Integer min,
                          @RequestParam(value = "max", required = false) Integer max){
        BPM existingBPM = bpmService.findById(id);

        if(existingBPM == null || !existingBPM.getCreatedBy().getEmail().equals(currentUser.getUsername())){
            return "redirect:/mybpms";
        }

        try {
            existingBPM.setBpmValue(bpmService.generateRandomBPMValue(min, max));
            bpmService.updateBPM(existingBPM);
            return "redirect:/mybpms";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Min BPM must be less than Max BPM."); // Przekazujemy komunikat błędu
            model.addAttribute("bpm", existingBPM); // Aby formularz nie był pusty
            return "editBPM"; // Wracamy do formularza z błędem
        }
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
