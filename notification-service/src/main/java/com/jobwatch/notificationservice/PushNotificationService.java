package com.jobwatch.notificationservice;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class PushNotificationService {

    private final PushService pushService;
    private final RestTemplate restTemplate;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    public PushNotificationService(
            @Value("${vapid.public-key}") String publicKey,
            @Value("${vapid.private-key}") String privateKey,
            @Value("${vapid.subject}") String subject) throws Exception {
        this.pushService = new PushService(publicKey, privateKey, subject);
        this.restTemplate = new RestTemplate();
    }

    public void notifySubscribersForCompany(String companySlug, String companyName, String companyLogo, String jobTitle) {
        String url = apiBaseUrl + "/api/push/internal/subscriptions?companySlug=" + companySlug;

        List<Map> subscriptions = restTemplate.getForObject(url, List.class);
        if (subscriptions == null || subscriptions.isEmpty()) return;

        String payload = String.format(
            "{\"title\":\"%s\",\"body\":\"%s\",\"icon\":\"%s\"}",
            companyName != null ? companyName : companySlug,
            jobTitle != null ? jobTitle : "New job posted",
            companyLogo != null ? companyLogo : ""
        );

        for (Map sub : subscriptions) {
            try {
                String endpoint = (String) sub.get("endpoint");
                String p256dh = (String) sub.get("p256dh");
                String auth = (String) sub.get("auth");

                Subscription subscription = new Subscription(endpoint,
                        new Subscription.Keys(p256dh, auth));
                Notification notification = new Notification(subscription, payload);
                pushService.send(notification);
            } catch (Exception e) {
                System.err.println("Failed to send push to subscription: " + e.getClass().getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
