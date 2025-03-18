package com.example.beathelper.services;

import com.example.beathelper.entities.User;
import com.example.beathelper.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRegistrationDate(LocalDateTime.now());
        user.setBanned(false);
        user.setDeleted(false);
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }
    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public User update(User user){
        return userRepository.save(user);
    }

    public void softDelete(User user){
        //userRepository.delete(user);
        user.setDeleted(true);
        user.setUsername("DELETED-USER-" + user.getId());
        user.setEmail("DELETED-USER-" + user.getId() + "@beathelper.com");
        user.setPassword(passwordEncoder.encode("DELETED-PASSWORD"));
        userRepository.save(user);
    }

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

    public User findById(Long id){
        return userRepository.findById(id).orElse(null);
    }
}
