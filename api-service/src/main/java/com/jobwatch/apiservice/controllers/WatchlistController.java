package com.jobwatch.apiservice.controllers;

import com.jobwatch.apiservice.models.Company;
import com.jobwatch.apiservice.models.Watchlist;
import com.jobwatch.apiservice.services.WatchlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    // Get current user's watchlist
    @GetMapping
    public ResponseEntity<List<Watchlist>> getWatchlist() {
        String email = getEmail();
        return ResponseEntity.ok(watchlistService.getWatchlist(email));
    }

    // Add company to watchlist
    @PostMapping
    public ResponseEntity<Watchlist> addToWatchlist(@RequestBody Map<String, Long> request) {
        String email = getEmail();
        Long companyId = request.get("company_id");
        return ResponseEntity.ok(watchlistService.addToWatchlist(email, companyId));
    }

    // Remove company from watchlist
    @DeleteMapping("/{companyId}")
    public ResponseEntity<?> removeFromWatchlist(@PathVariable Long companyId) {
        String email = getEmail();
        watchlistService.removeFromWatchlist(email, companyId);
        return ResponseEntity.ok(Map.of("message", "Removed from watchlist"));
    }

    // Search companies
    @GetMapping("/search")
    public ResponseEntity<List<Company>> searchCompanies(@RequestParam String q) {
        return ResponseEntity.ok(watchlistService.searchCompanies(q));
    }

    // Helper to get logged in user's email from JWT
    private String getEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();
    }
}