package com.jobwatch.apiservice.repositories;

import com.jobwatch.apiservice.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {
    Optional<Job> findByExternalId(String externalId);
    boolean existsByExternalId(String externalId);
}
