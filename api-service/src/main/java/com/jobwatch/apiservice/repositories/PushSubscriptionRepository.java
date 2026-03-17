package com.jobwatch.apiservice.repositories;

import com.jobwatch.apiservice.models.PushSubscription;
import com.jobwatch.apiservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    List<PushSubscription> findByUser(User user);
    boolean existsByUserAndEndpoint(User user, String endpoint);
}
