package com.jobwatch.apiservice.repositories;

import com.jobwatch.apiservice.models.Job;
import com.jobwatch.apiservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {
    Optional<Job> findByExternalId(String externalId);
    boolean existsByExternalId(String externalId);

    @Query("SELECT j.externalId FROM Job j WHERE j.company.slug = :companySlug")
    List<String> findExternalIdsByCompanySlug(@Param("companySlug") String companySlug);

    @Modifying
    @Transactional
    @Query("DELETE FROM Job j WHERE j.company.slug = :companySlug AND j.externalId NOT IN :liveIds")
    void deleteStaleByCompanySlug(@Param("companySlug") String companySlug, @Param("liveIds") Collection<String> liveIds);

    @Query("SELECT j FROM Job j WHERE j.company IN " +
           "(SELECT w.company FROM Watchlist w WHERE w.user = :user) " +
           "ORDER BY j.updatedAt DESC")
    List<Job> findJobsForWatchlist(@Param("user") User user);

    @Query("SELECT j FROM Job j WHERE j.company IN " +
           "(SELECT w.company FROM Watchlist w WHERE w.user = :user) " +
           "AND (:#{#categories == null || #categories.isEmpty()} = true OR j.category IN :categories) " +
           "AND (:#{#seniorities == null || #seniorities.isEmpty()} = true OR j.seniority IN :seniorities) " +
           "AND (:usOnly = false OR j.isUS = true) " +
           "ORDER BY j.updatedAt DESC")
    List<Job> findJobsForWatchlistFiltered(@Param("user") User user,
                                           @Param("categories") Collection<String> categories,
                                           @Param("seniorities") Collection<String> seniorities,
                                           @Param("usOnly") boolean usOnly);
}
