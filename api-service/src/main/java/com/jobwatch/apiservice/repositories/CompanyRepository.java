package com.jobwatch.apiservice.repositories;

import com.jobwatch.apiservice.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findBySlug(String slug);
    List<Company> findByNameContainingIgnoreCase(String name);
}