package com.jobwatch.apiservice.repositories;

import com.jobwatch.apiservice.models.UnsupportedCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnsupportedCompanyRepository extends JpaRepository<UnsupportedCompany, Long> {
    boolean existsBySlug(String slug);
}
