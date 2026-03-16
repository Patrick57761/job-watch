package com.jobwatch.apiservice.repositories;

import com.jobwatch.apiservice.models.Job;
import com.jobwatch.apiservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {
    Optional<Job> findByExternalId(String externalId);
    boolean existsByExternalId(String externalId);

    @Query("SELECT j FROM Job j WHERE j.company IN " +
           "(SELECT w.company FROM Watchlist w WHERE w.user = :user) " +
           "ORDER BY j.createdAt DESC")
    List<Job> findJobsForWatchlist(@Param("user") User user);
}
