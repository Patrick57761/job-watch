# JobWatch

Real-time job alert platform. Watchlist companies and get push notifications within 60 seconds of a new posting on Greenhouse, Lever, or Ashby.

Many job boards and GitHub lists still surface postings that are weeks or months old—roles that are already closed or flooded with applicants. JobWatch pulls directly from company hiring APIs and alerts you the moment a role goes live, so you can always be the first to apply, or check if a posting is outdated. 

**Live:** https://jobwatch.duckdns.org

---

## Stack & Structure

Java · Spring Boot · Python · Next.js · TypeScript · PostgreSQL · Redis · Docker · AWS EC2

```
job-watch/
├── api-service/          # Java / Spring Boot — REST API, JWT auth, watchlist, job storage
├── scraper-service/      # Python — polls ATS APIs every 60s, deduplicates via Redis
├── notification-service/ # Java / Spring Boot — sends Web Push notifications
├── frontend/             # Next.js — auth, watchlist UI, jobs feed
├── docker-compose.yml    # Orchestrates all services + Redis + PostgreSQL
└── docs/                 # Architecture decisions, data model, service breakdown
```

---

## Features

- Jobs feed includes all relevant roles with posting date
- Shortlisted push notifications within 60 seconds of a new job posting
- Filter by job category, seniority, and US-only; preferences are saved across sessions
- Covers companies hiring on Greenhouse, Lever, or Ashby
- Add/remove/mute companies from customized watchlist

---

## Running Locally

**Prerequisites:** Docker, Docker Compose

```bash
git clone https://github.com/Patrick57761/job-watch.git
cd job-watch
docker-compose up --build
```

Frontend → http://localhost:3000 · API → http://localhost:8080

> Push notifications require HTTPS and will not work on localhost.
