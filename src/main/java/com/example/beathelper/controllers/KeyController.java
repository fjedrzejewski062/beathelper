package com.example.beathelper.controllers;

import com.example.beathelper.entities.BPM;
import com.example.beathelper.entities.Key;
import com.example.beathelper.entities.User;
import com.example.beathelper.enums.KeyType;
import com.example.beathelper.services.KeyService;
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

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class KeyController {
    private final KeyService keyService;
    private final UserService userService;

    public KeyController(KeyService keyService, UserService userService) {
        this.keyService = keyService;
        this.userService = userService;
    }

    @GetMapping("/mykeys")
    public String myKeys(Model model,
                         @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser,
                         RedirectAttributes redirectAttributes,
                         @RequestParam(required = false) String keyName,
                         @RequestParam(required = false) String startDate,
                         @RequestParam(required = false) String endDate,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "id") String sortField,
                         @RequestParam(defaultValue = "asc") String sortDirection){

        String email = currentUser.getUsername();
        User user = userService.findByEmail(email).orElse(null);

        if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            if (end.isBefore(start)) {
                redirectAttributes.addFlashAttribute("error", "End date cannot be earlier than start date.");
                return "redirect:/mykeys"; // Używamy addFlashAttribute do przekazania komunikatu
            }
        }

        Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, 10, sort);

        KeyType keyType = null;
        if (keyName != null && !keyName.isEmpty()) {
            try {
                keyType = KeyType.valueOf(keyName); // Konwertowanie stringa na enum
            } catch (IllegalArgumentException e) {
                System.out.println("Niepoprawny typ klucza: " + keyName);
            }
        }

        // Przekazujemy keyType do metody findFilteredKeys
        Page<Key> keyPage = keyService.findFilteredKeys(user, keyType, startDate, endDate, pageable);

        // Jeśli brak wyników, dodaj komunikat do modelu
//        if (keyPage.getTotalElements() == 0) {
//            model.addAttribute("error", "No keys found matching the given criteria.");
//        }

        List<Key> keys = keyService.findKeysByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("keys", keyPage.getContent());
        model.addAttribute("totalPages", keyPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyName", keyName);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "myKeys";
    }

    @GetMapping("/mykeys/random")
    public String showRandomKey(Model model){
        model.addAttribute("key", new Key());
        return "randomKey";
    }

    @PostMapping("/mykeys/random")
    public String randomKey(@AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser,
                            Model model,
                            @RequestParam(value = "type", required = false) String type){

        System.out.println("Wywołano endpoint POST /mykeys/random");

        String email = currentUser.getUsername();
        System.out.println("Aktualny użytkownik: " + email);

        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            System.out.println("Nie znaleziono użytkownika w bazie!");
            return "redirect:/mykeys";
        }

        Key generatedKey = keyService.randomKey(user, type);
        if (generatedKey == null) {
            System.out.println("Błąd: nie wygenerowano klucza!");
        } else {
            System.out.println("Wygenerowano i zapisano klucz o ID: " + generatedKey.getId());
        }

        return "redirect:/mykeys";
    }
    @GetMapping("/mykeys/edit/{id}")
    public String showEditKey(@PathVariable Long id, Model model,
                              @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser){
        Key key = keyService.findById(id);

        if(key == null || !key.getCreatedBy().getEmail().equals(currentUser.getUsername())){
            return "redirect:/mykeys";
        }

        model.addAttribute("key", key);
        return "editKey";
    }

    @PostMapping("/mykeys/edit/{id}")
    public String editKey(@PathVariable Long id,
                          @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser,
                          Model model,
                          @RequestParam(value = "type", required = false) String type){
        Key existingKey = keyService.findById(id);

        if(existingKey == null || !existingKey.getCreatedBy().getEmail().equals(currentUser.getUsername())){
            return "redirect:/mykeys";
        }

        KeyType newKeyType = keyService.getRandomKeyType(type);
        List<KeyType> newRelatedKeys = keyService.findRelatedKeys(newKeyType);

        existingKey.setName(newKeyType);
        existingKey.getRelatedKeys().clear();
        existingKey.getRelatedKeys().addAll(newRelatedKeys);

        keyService.updateKey(existingKey);
        return "redirect:/mykeys";
    }

    @GetMapping("/mykeys/delete/{id}")
    public String deleteKey(@PathVariable Long id,
                            @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser){
        Key key = keyService.findById(id);
        if(key != null && key.getCreatedBy().getEmail().equals(currentUser.getUsername())){
            keyService.deleteKey(key);
        }
        return "redirect:/mykeys";
    }

}
