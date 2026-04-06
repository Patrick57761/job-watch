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
           "AND (:usOnly = false OR (" +
               "LOWER(j.location) LIKE '%united states%' OR LOWER(j.location) LIKE '%usa%' OR LOWER(j.location) LIKE '%u.s.%' OR " +
               "LOWER(j.location) LIKE '%, us' OR LOWER(j.location) LIKE '%, us;%' OR LOWER(j.location) LIKE '%us remote%' OR LOWER(j.location) LIKE '%remote, us%' OR " +
               "LOWER(j.location) LIKE '%remote%' OR " +
               "LOWER(j.location) LIKE '%alabama%' OR LOWER(j.location) LIKE '%, al' OR LOWER(j.location) LIKE '%, al;%' OR " +
               "LOWER(j.location) LIKE '%alaska%' OR LOWER(j.location) LIKE '%, ak' OR LOWER(j.location) LIKE '%, ak;%' OR " +
               "LOWER(j.location) LIKE '%arizona%' OR LOWER(j.location) LIKE '%, az' OR LOWER(j.location) LIKE '%, az;%' OR LOWER(j.location) LIKE '%, az %' OR " +
               "LOWER(j.location) LIKE '%arkansas%' OR LOWER(j.location) LIKE '%, ar' OR LOWER(j.location) LIKE '%, ar;%' OR " +
               "LOWER(j.location) LIKE '%california%' OR LOWER(j.location) LIKE '%, ca' OR LOWER(j.location) LIKE '%, ca;%' OR LOWER(j.location) LIKE '%, ca %' OR " +
               "LOWER(j.location) LIKE '%colorado%' OR LOWER(j.location) LIKE '%, co' OR LOWER(j.location) LIKE '%, co;%' OR " +
               "LOWER(j.location) LIKE '%connecticut%' OR LOWER(j.location) LIKE '%, ct' OR LOWER(j.location) LIKE '%, ct;%' OR " +
               "LOWER(j.location) LIKE '%delaware%' OR LOWER(j.location) LIKE '%, de' OR LOWER(j.location) LIKE '%, de;%' OR " +
               "LOWER(j.location) LIKE '%florida%' OR LOWER(j.location) LIKE '%, fl' OR LOWER(j.location) LIKE '%, fl;%' OR LOWER(j.location) LIKE '%, fl %' OR " +
               "LOWER(j.location) LIKE '%georgia%' OR LOWER(j.location) LIKE '%, ga' OR LOWER(j.location) LIKE '%, ga;%' OR LOWER(j.location) LIKE '%, ga %' OR " +
               "LOWER(j.location) LIKE '%hawaii%' OR LOWER(j.location) LIKE '%, hi' OR LOWER(j.location) LIKE '%, hi;%' OR " +
               "LOWER(j.location) LIKE '%idaho%' OR LOWER(j.location) LIKE '%, id' OR LOWER(j.location) LIKE '%, id;%' OR " +
               "LOWER(j.location) LIKE '%illinois%' OR LOWER(j.location) LIKE '%, il' OR LOWER(j.location) LIKE '%, il;%' OR LOWER(j.location) LIKE '%, il %' OR " +
               "LOWER(j.location) LIKE '%indiana%' OR LOWER(j.location) LIKE '%, in' OR LOWER(j.location) LIKE '%, in;%' OR " +
               "LOWER(j.location) LIKE '%iowa%' OR LOWER(j.location) LIKE '%, ia' OR LOWER(j.location) LIKE '%, ia;%' OR " +
               "LOWER(j.location) LIKE '%kansas%' OR LOWER(j.location) LIKE '%, ks' OR LOWER(j.location) LIKE '%, ks;%' OR " +
               "LOWER(j.location) LIKE '%kentucky%' OR LOWER(j.location) LIKE '%, ky' OR LOWER(j.location) LIKE '%, ky;%' OR " +
               "LOWER(j.location) LIKE '%louisiana%' OR LOWER(j.location) LIKE '%, la' OR LOWER(j.location) LIKE '%, la;%' OR " +
               "LOWER(j.location) LIKE '%maine%' OR LOWER(j.location) LIKE '%, me' OR LOWER(j.location) LIKE '%, me;%' OR " +
               "LOWER(j.location) LIKE '%maryland%' OR LOWER(j.location) LIKE '%, md' OR LOWER(j.location) LIKE '%, md;%' OR " +
               "LOWER(j.location) LIKE '%massachusetts%' OR LOWER(j.location) LIKE '%, ma' OR LOWER(j.location) LIKE '%, ma;%' OR " +
               "LOWER(j.location) LIKE '%michigan%' OR LOWER(j.location) LIKE '%, mi' OR LOWER(j.location) LIKE '%, mi;%' OR " +
               "LOWER(j.location) LIKE '%minnesota%' OR LOWER(j.location) LIKE '%, mn' OR LOWER(j.location) LIKE '%, mn;%' OR " +
               "LOWER(j.location) LIKE '%mississippi%' OR LOWER(j.location) LIKE '%, ms' OR LOWER(j.location) LIKE '%, ms;%' OR " +
               "LOWER(j.location) LIKE '%missouri%' OR LOWER(j.location) LIKE '%, mo' OR LOWER(j.location) LIKE '%, mo;%' OR " +
               "LOWER(j.location) LIKE '%montana%' OR LOWER(j.location) LIKE '%, mt' OR LOWER(j.location) LIKE '%, mt;%' OR " +
               "LOWER(j.location) LIKE '%nebraska%' OR LOWER(j.location) LIKE '%, ne' OR LOWER(j.location) LIKE '%, ne;%' OR " +
               "LOWER(j.location) LIKE '%nevada%' OR LOWER(j.location) LIKE '%, nv' OR LOWER(j.location) LIKE '%, nv;%' OR " +
               "LOWER(j.location) LIKE '%new hampshire%' OR LOWER(j.location) LIKE '%, nh' OR LOWER(j.location) LIKE '%, nh;%' OR " +
               "LOWER(j.location) LIKE '%new jersey%' OR LOWER(j.location) LIKE '%, nj' OR LOWER(j.location) LIKE '%, nj;%' OR LOWER(j.location) LIKE '%, nj %' OR " +
               "LOWER(j.location) LIKE '%new mexico%' OR LOWER(j.location) LIKE '%, nm' OR LOWER(j.location) LIKE '%, nm;%' OR " +
               "LOWER(j.location) LIKE '%new york%' OR LOWER(j.location) LIKE '%, ny' OR LOWER(j.location) LIKE '%, ny;%' OR LOWER(j.location) LIKE '%, ny %' OR " +
               "LOWER(j.location) LIKE '%north carolina%' OR LOWER(j.location) LIKE '%, nc' OR LOWER(j.location) LIKE '%, nc;%' OR " +
               "LOWER(j.location) LIKE '%north dakota%' OR LOWER(j.location) LIKE '%, nd' OR LOWER(j.location) LIKE '%, nd;%' OR " +
               "LOWER(j.location) LIKE '%ohio%' OR LOWER(j.location) LIKE '%, oh' OR LOWER(j.location) LIKE '%, oh;%' OR " +
               "LOWER(j.location) LIKE '%oklahoma%' OR LOWER(j.location) LIKE '%, ok' OR LOWER(j.location) LIKE '%, ok;%' OR " +
               "LOWER(j.location) LIKE '%oregon%' OR LOWER(j.location) LIKE '%, or' OR LOWER(j.location) LIKE '%, or;%' OR " +
               "LOWER(j.location) LIKE '%pennsylvania%' OR LOWER(j.location) LIKE '%, pa' OR LOWER(j.location) LIKE '%, pa;%' OR LOWER(j.location) LIKE '%, pa %' OR " +
               "LOWER(j.location) LIKE '%rhode island%' OR LOWER(j.location) LIKE '%, ri' OR LOWER(j.location) LIKE '%, ri;%' OR " +
               "LOWER(j.location) LIKE '%south carolina%' OR LOWER(j.location) LIKE '%, sc' OR LOWER(j.location) LIKE '%, sc;%' OR " +
               "LOWER(j.location) LIKE '%south dakota%' OR LOWER(j.location) LIKE '%, sd' OR LOWER(j.location) LIKE '%, sd;%' OR " +
               "LOWER(j.location) LIKE '%tennessee%' OR LOWER(j.location) LIKE '%, tn' OR LOWER(j.location) LIKE '%, tn;%' OR " +
               "LOWER(j.location) LIKE '%texas%' OR LOWER(j.location) LIKE '%, tx' OR LOWER(j.location) LIKE '%, tx;%' OR LOWER(j.location) LIKE '%, tx %' OR " +
               "LOWER(j.location) LIKE '%utah%' OR LOWER(j.location) LIKE '%, ut' OR LOWER(j.location) LIKE '%, ut;%' OR " +
               "LOWER(j.location) LIKE '%vermont%' OR LOWER(j.location) LIKE '%, vt' OR LOWER(j.location) LIKE '%, vt;%' OR " +
               "LOWER(j.location) LIKE '%virginia%' OR LOWER(j.location) LIKE '%, va' OR LOWER(j.location) LIKE '%, va;%' OR LOWER(j.location) LIKE '%, va %' OR " +
               "LOWER(j.location) LIKE '%washington%' OR LOWER(j.location) LIKE '%, wa' OR LOWER(j.location) LIKE '%, wa;%' OR LOWER(j.location) LIKE '%, wa %' OR " +
               "LOWER(j.location) LIKE '%west virginia%' OR LOWER(j.location) LIKE '%, wv' OR LOWER(j.location) LIKE '%, wv;%' OR " +
               "LOWER(j.location) LIKE '%wisconsin%' OR LOWER(j.location) LIKE '%, wi' OR LOWER(j.location) LIKE '%, wi;%' OR " +
               "LOWER(j.location) LIKE '%wyoming%' OR LOWER(j.location) LIKE '%, wy' OR LOWER(j.location) LIKE '%, wy;%' OR " +
               "LOWER(j.location) LIKE '%district of columbia%' OR LOWER(j.location) LIKE '%, dc' OR LOWER(j.location) LIKE '%, dc;%')) " +
           "ORDER BY j.updatedAt DESC")
    List<Job> findJobsForWatchlistFiltered(@Param("user") User user,
                                           @Param("categories") Collection<String> categories,
                                           @Param("seniorities") Collection<String> seniorities,
                                           @Param("usOnly") boolean usOnly);
}
