package com.jobwatch.apiservice.repositories;

import com.jobwatch.apiservice.models.Watchlist;
import com.jobwatch.apiservice.models.User;
import com.jobwatch.apiservice.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    List<Watchlist> findByUser(User user);
    List<Watchlist> findByCompany(Company company);
    Optional<Watchlist> findByUserAndCompany(User user, Company company);
    boolean existsByUserAndCompany(User user, Company company);
}