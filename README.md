# Beatmaker 🎶

**Beatmaker** is a Java Spring + Thymeleaf web application designed for music creators.  
It helps artists generate musical ideas by randomly selecting BPM (Beats Per Minute) and Key, with a smart recommendation system based on the Camelot Wheel for harmonic mixing.

## Features 🚀

### BPM
- **Random BPM Generator**: Get inspired with a randomly selected BPM.
- **My BPMs**: View, add, edit, and delete all your saved BPMs.

### Key
- **Random Key Generator**: Randomly select a musical key, along with 3 related keys based on the Camelot Circle logic for easy harmonic transitions.
- **My Keys**: View, add, edit, and delete your saved keys and their twin keys.

### Donate
- **Donate**: Support the app and its future development by donating through the **Donate** section.

### Admin Dashboard
- **Admin Panel**: Access a secure dashboard for managing BPMs, Keys, and user interactions.

## How it works? 🔍

1. **Generate Inspiration**: Use the random generator to select a BPM and a Key when starting a new beat or track.
2. **Harmonic Mixing Support**: When a Key is generated, 3 harmonically compatible related keys are suggested based on Camelot Wheel theory.
3. **Organize Your Ideas**: Save generated BPMs and Keys into your personal lists, accessible under "My BPMs" and "My Keys" tabs.
4. **Edit and Manage**: Easily add, edit, or remove BPMs and Keys as your projects evolve.
5. **Support Development**: If you enjoy the app, you can support further updates by donating through the "Donate" tab, powered by Stripe integration.
6. **Admin Dashboard**: As an administrator, you have access to a dedicated panel (dashboard) where you can manage all BPM and Key records, monitor activities, and ensure the smooth operation of the app.

## Tech Stack 🛠

- **Java 21**
- **Maven** – Build and dependency management
- **Spring Boot 3.4.3** – Core framework
- **Spring MVC** – Web framework (part of Spring Boot Starter Web)
- **Spring Security** – Authentication and authorization
- **Spring Boot Starter Thymeleaf** – Server-side templating
- **Thymeleaf Extras Spring Security 6** – Security integration with Thymeleaf views
- **Spring Data JPA** – Data persistence
- **Spring Boot Starter Validation** – Bean validation with Hibernate Validator
- **Hibernate** – ORM for data handling
- **H2 Database** – In-memory database for development and testing
- **Lombok** – Boilerplate code reduction
- **Stripe Java SDK** – Stripe API integration for donation payments
- **JUnit 5** – Modern testing framework
- **AssertJ** – Fluent assertions for better test readability
- **Mockito** – Mocking framework for unit tests
- **Spring Boot Starter Test** – Spring Boot testing support
- **Spring Security Test** – Testing utilities for Spring Security
- **Testcontainers** – Integration tests with real database containers

## How to Run ⚙️

1. Clone the repository:
    ```bash
    git clone https://github.com/fjedrzejewski062/beatmaker.git
    ```
2. Navigate into the project folder:
    ```bash
    cd beatmaker
    ```
3. Run the application:
    ```bash
    ./mvnw spring-boot:run
    ```
4. Open your browser and go to:
    ```
    http://localhost:8080
    ```

---

Made with ❤️ for music creators.
