# JobWatch — Architecture

## What It Does
JobWatch lets users build a watchlist of specific companies and receive push notifications
within 60 seconds of a relevant job posting on Greenhouse, Lever, or Ashby.

Target users: CS students and new grads targeting specific companies for internships and new grad roles.

---

## User Flow

- Create an account and set up a profile (target role, skills, locations)
- Search for companies and add them to my watchlist
- Set preferences (location, role type, keywords)
- Receive a push notification within 60 seconds of a relevant job posting
- See recommended jobs and companies based on my profile
- Save or dismiss jobs to improve future recommendations

---

## Build Stage
V1
- Greenhouse + Lever + Ashby scrapers
- User auth + watchlist management
- Basic preference filtering
- PWA push notifications
- Job feed in the app
- Docker Compose local setup
- Basic AWS deployment

V2

- Workday scraper
- ML job recommendations
- Email notification fallback
- Observability dashboard (scrape latency, jobs found, delivery rate)

V3

- SMS notifications via Twilio
- Custom site scrapers (Playwright)
- Collaborative filtering once user interaction data exists

## Services

```
job-watch/
├── scraper-service/       # Python — polls Greenhouse, Lever, Ashby
├── api-service/           # Spring Boot — REST API, auth, watchlists
├── notification-service/  # Spring Boot — Kafka consumer, push notifications
├── ml-service/            # Python — job recommendations
├── frontend/              # Next.js — PWA with push notification support
├── infrastructure/        # Docker, docker-compose
├── docs/                  # This file and diagrams
├── docker-compose.yml
└── README.md
```

---

## Architecture Diagram

```
User (PWA)
    |
    | REST
    v
API Service (Spring Boot) <——> MySQL (users, watchlists, jobs)
                          <——> Redis (sessions, rate limiting)
    |
    | reads watchlist
    v
Scraper Service (Python)
    | polls every minute
    v
Greenhouse / Lever / Ashby APIs
    |
    | new job found
    v
Redis 
    |
    | if new
    v
Kafka topic: "jobs.new"
    |
    v
Notification Service (Spring Boot)
    | consumes Kafka
    | matches job to users watching that company
    | applies preference filters (location, role type)
    v
Web Push API ——> User's Phone/Browser (PWA notification)

ML Service (Python)
    | embeds user profile + jobs using sentence-transformers
    | ranks by cosine similarity
    v
Recommended jobs shown in app
```

---

## How a Job Goes From Posted to Notification

```
1. Recruiter publishes job on Greenhouse
2. Scraper polls Greenhouse API
3. Check Redis: have I seen this job ID before?
   - Yes → skip
   - No  → add to Redis, continue
4. Publish job to Kafka topic "jobs.new"
5. Notification Service reads from Kafka
6. Query MySQL: who is watching this company?
7. For each user: does this job match their preferences?
   - Location match?
   - Role type match? (intern / new grad)
   - Keyword match?
8. If yes → send push notification via Web Push API
9. User receives notification on their phone
10. User clicks → opens job application URL
```

---

## Tech Stack

| Technology | What It Does | Why |
|-----------|-------------|-----|
| Spring Boot | REST API + notification consumer | Industry standard Java backend |
| Python | Scraper + ML | Best libraries for HTTP scraping and ML |
| FastAPI | ML service API | Lightweight Python API framework |
| MySQL | Main database | Stores users, watchlists, jobs permanently |
| Redis | Fast deduplication + caching | O(1) lookup to check if job was already seen |
| Kafka | Event streaming | Decouples scraper from notifications |
| HuggingFace sentence-transformers | Job similarity matching | Pre-trained ML model, no training data needed |
| PyTorch | Powers HuggingFace under the hood | Real ML framework exposure |
| Next.js + React | Frontend PWA | Modern standard, supports push notifications |
| Docker + Docker Compose | Run all services locally | One command starts everything |
| AWS | Cloud hosting | Industry standard, managed services |
| GitHub Actions | CI/CD | Auto-run tests and deploy on push |

---

## Database Tables

### users
| column | type | notes |
|--------|------|-------|
| id | BIGINT | primary key |
| email | VARCHAR | unique |
| password_hash | VARCHAR | never store plain text |
| created_at | TIMESTAMP | |

### user_profiles
| column | type | notes |
|--------|------|-------|
| user_id | BIGINT | references users |
| target_role | VARCHAR | e.g. "Software Engineer Intern" |
| graduation_year | INT | |
| skills | JSON | ["Python", "React", "AWS"] |
| preferred_industries | JSON | ["Fintech", "AI/ML"] |

### companies
| column | type | notes |
|--------|------|-------|
| id | BIGINT | primary key |
| name | VARCHAR | e.g. "Stripe" |
| platform | ENUM | greenhouse / lever / ashby / workday |
| platform_id | VARCHAR | e.g. "stripe" used in API calls |
| logo_url | VARCHAR | |

### watchlists
| column | type | notes |
|--------|------|-------|
| user_id | BIGINT | references users |
| company_id | BIGINT | references companies |
| created_at | TIMESTAMP | |

### user_preferences
| column | type | notes |
|--------|------|-------|
| user_id | BIGINT | references users |
| locations | JSON | ["San Francisco", "Remote"] |
| job_types | JSON | ["intern", "new_grad"] |
| keywords | JSON | ["backend", "ML"] |
| max_notifs_per_day | INT | default 20 |

### jobs
| column | type | notes |
|--------|------|-------|
| id | BIGINT | primary key |
| external_id | VARCHAR | ATS job ID |
| company_id | BIGINT | references companies |
| title | VARCHAR | |
| location | VARCHAR | |
| is_remote | BOOLEAN | |
| job_type | ENUM | intern / new_grad / full_time |
| url | VARCHAR | direct link to apply |
| source | ENUM | greenhouse / lever / ashby |
| posted_at | TIMESTAMP | |
| detected_at | TIMESTAMP | when scraper found it |

### user_interactions
| column | type | notes |
|--------|------|-------|
| user_id | BIGINT | |
| job_id | BIGINT | |
| action | ENUM | viewed / saved / dismissed / applied |
| created_at | TIMESTAMP | used for ML re-ranking later |

### notifications
| column | type | notes |
|--------|------|-------|
| user_id | BIGINT | |
| job_id | BIGINT | |
| channel | ENUM | push / email / sms |
| status | ENUM | pending / delivered / failed |
| delivered_at | TIMESTAMP | |

### push_subscriptions
| column | type | notes |
|--------|------|-------|
| user_id | BIGINT | |
| endpoint | VARCHAR | browser push endpoint |
| p256dh_key | VARCHAR | encryption key |
| auth_key | VARCHAR | authentication key |