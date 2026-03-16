package com.jobwatch.apiservice.controllers;

import com.jobwatch.apiservice.dto.DtoMapper;
import com.jobwatch.apiservice.dto.WatchlistDTO;
import com.jobwatch.apiservice.models.Company;
import com.jobwatch.apiservice.services.WatchlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public ResponseEntity<List<WatchlistDTO>> getWatchlist() {
        String email = getEmail();
        List<WatchlistDTO> watchlist = watchlistService.getWatchlist(email)
                .stream()
                .map(DtoMapper::toWatchlistDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(watchlist);
    }

    @PostMapping
    public ResponseEntity<WatchlistDTO> addToWatchlist(@RequestBody Map<String, Long> request) {
        String email = getEmail();
        Long companyId = request.get("company_id");
        return ResponseEntity.ok(
                DtoMapper.toWatchlistDTO(watchlistService.addToWatchlist(email, companyId))
        );
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<?> removeFromWatchlist(@PathVariable Long companyId) {
        String email = getEmail();
        watchlistService.removeFromWatchlist(email, companyId);
        return ResponseEntity.ok(Map.of("message", "Removed from watchlist"));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Company>> searchCompanies(@RequestParam String q) {
        return ResponseEntity.ok(watchlistService.searchCompanies(q));
    }

    private String getEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();
    }
}