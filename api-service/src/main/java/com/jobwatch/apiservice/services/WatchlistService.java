package com.jobwatch.apiservice.services;

import com.jobwatch.apiservice.models.Company;
import com.jobwatch.apiservice.models.User;
import com.jobwatch.apiservice.models.Watchlist;
import com.jobwatch.apiservice.repositories.CompanyRepository;
import com.jobwatch.apiservice.repositories.UserRepository;
import com.jobwatch.apiservice.repositories.WatchlistRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public WatchlistService(WatchlistRepository watchlistRepository,
                            CompanyRepository companyRepository,
                            UserRepository userRepository) {
        this.watchlistRepository = watchlistRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    // Get all companies in watchlist
    public List<Watchlist> getWatchlist(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return watchlistRepository.findByUser(user);
    }

    // Add a company to watchlist
    public Watchlist addToWatchlist(String email, Long companyId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (watchlistRepository.existsByUserAndCompany(user, company)) {
            throw new RuntimeException("Company already in watchlist");
        }

        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);
        watchlist.setCompany(company);
        return watchlistRepository.save(watchlist);
    }

    // Remove a company from watchlist
    public void removeFromWatchlist(String email, Long companyId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Watchlist watchlist = watchlistRepository.findByUserAndCompany(user, company)
                .orElseThrow(() -> new RuntimeException("Company not in watchlist"));

        watchlistRepository.delete(watchlist);
    }

    // Search companies by name
    public List<Company> searchCompanies(String query) {
        return companyRepository.findByNameContainingIgnoreCase(query);
    }
}