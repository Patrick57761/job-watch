package com.jobwatch.apiservice.controllers;

import com.jobwatch.apiservice.models.Job;
import com.jobwatch.apiservice.services.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<?> ingestJob(@RequestBody JobIngestRequest request) {
        Job job = jobService.ingestJob(
                request.externalId(),
                request.title(),
                request.location(),
                request.url(),
                request.updatedAt(),
                request.platform(),
                request.companySlug()
        );

        if (job == null) {
            return ResponseEntity.ok("duplicate");
        }

        return ResponseEntity.ok(job);
    }

    record JobIngestRequest(
            String externalId,
            String title,
            String location,
            String url,
            String updatedAt,
            String platform,
            String companySlug
    ) {}
}
