package com.jobwatch.apiservice.controllers;

import com.jobwatch.apiservice.models.User;
import com.jobwatch.apiservice.models.UserPreferences;
import com.jobwatch.apiservice.repositories.UserPreferencesRepository;
import com.jobwatch.apiservice.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class PreferencesController {

    private final UserPreferencesRepository preferencesRepository;
    private final UserRepository userRepository;

    public PreferencesController(UserPreferencesRepository preferencesRepository,
                                 UserRepository userRepository) {
        this.preferencesRepository = preferencesRepository;
        this.userRepository = userRepository;
    }

    // User fetches their own preferences
    @GetMapping("/api/preferences")
    public ResponseEntity<PreferencesResponse> getPreferences(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return preferencesRepository.findByUser(user)
                .map(p -> ResponseEntity.ok(toResponse(p)))
                .orElse(ResponseEntity.ok(new PreferencesResponse(List.of(), List.of())));
    }

    // User saves their preferences
    @PostMapping("/api/preferences")
    public ResponseEntity<PreferencesResponse> savePreferences(
            @RequestBody PreferencesRequest request,
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserPreferences prefs = preferencesRepository.findByUser(user)
                .orElse(new UserPreferences());
        prefs.setUser(user);
        prefs.setCategories(String.join(",", request.categories()));
        prefs.setSeniorities(String.join(",", request.seniorities()));
        preferencesRepository.save(prefs);

        return ResponseEntity.ok(toResponse(prefs));
    }

    // Internal — notification-service checks a user's preferences by email
    @GetMapping("/internal/preferences")
    public ResponseEntity<PreferencesResponse> getPreferencesInternal(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return preferencesRepository.findByUser(user)
                .map(p -> ResponseEntity.ok(toResponse(p)))
                .orElse(ResponseEntity.ok(new PreferencesResponse(List.of(), List.of())));
    }

    private PreferencesResponse toResponse(UserPreferences p) {
        List<String> cats = (p.getCategories() == null || p.getCategories().isBlank())
                ? List.of()
                : Arrays.asList(p.getCategories().split(","));
        List<String> sens = (p.getSeniorities() == null || p.getSeniorities().isBlank())
                ? List.of()
                : Arrays.asList(p.getSeniorities().split(","));
        return new PreferencesResponse(cats, sens);
    }

    record PreferencesRequest(List<String> categories, List<String> seniorities) {}
    record PreferencesResponse(List<String> categories, List<String> seniorities) {}
}
