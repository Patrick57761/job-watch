package com.jobwatch.notificationservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JobListener {

    private final PushNotificationService pushNotificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JobListener(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @KafkaListener(topics = "jobs.new", groupId = "notification-service")
    public void onNewJob(String message) {
        try {
            Map<String, Object> job = objectMapper.readValue(message, Map.class);
            String companySlug = (String) job.get("company_slug");
            if (companySlug != null) {
                pushNotificationService.notifySubscribersForCompany(companySlug);
            }
        } catch (Exception e) {
            System.err.println("Failed to process job message: " + e.getMessage());
        }
    }
}
