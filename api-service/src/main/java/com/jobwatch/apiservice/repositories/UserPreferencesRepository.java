package com.jobwatch.apiservice.repositories;

import com.jobwatch.apiservice.models.User;
import com.jobwatch.apiservice.models.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    Optional<UserPreferences> findByUser(User user);
}
