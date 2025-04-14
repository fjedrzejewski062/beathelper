package com.example.beathelper.repositories;

import com.example.beathelper.entities.User;
import com.example.beathelper.enums.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    // Pobieranie użytkowników z paginacją
    Page<User> findAll(Pageable pageable);

    // Wyszukiwanie użytkowników po nazwie użytkownika
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    Page<User> searchByUsername(@Param("username") String username, Pageable pageable);

    // Wyszukiwanie użytkowników po emailu
    @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    Page<User> searchByEmail(@Param("email") String email, Pageable pageable);

    // Wyszukiwanie użytkowników po dacie rejestracji
    Page<User> findByRegistrationDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // Wyszukiwanie użytkowników po dacie ostatniego logowania
    Page<User> findByLastLoginBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // Wyszukiwanie użytkowników po statusie zbanowania
    Page<User> findByBanned(Boolean banned, Pageable pageable);

    // Wyszukiwanie użytkowników po statusie usunięcia
    Page<User> findByDeleted(Boolean deleted, Pageable pageable);

    // Wyszukiwanie użytkowników po typie
    Page<User> findByUserType(UserType userType, Pageable pageable);

    // Wyszukiwanie użytkowników po roli
    Page<User> findByRole(String role, Pageable pageable);
    Page<User> findByBannedFalseAndDeletedFalse(Pageable pageable);
}
