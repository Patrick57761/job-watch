# JobWatch — Architecture

## Data Flow

```
ATS APIs → scraper-service (Python) → Redis dedup → api-service (Java) → PostgreSQL
                                                  → notification-service (Java) → Web Push → browser
```

## Services

| Service | Language | Role |
|---|---|---|
| api-service | Java / Spring Boot | REST API, JWT auth, watchlist, job storage |
| scraper-service | Python | Poll ATS APIs every 60s, deduplicate via Redis, persist jobs |
| notification-service | Java / Spring Boot | Receive job events via HTTP, send Web Push |
| frontend | Next.js / TypeScript | Auth, watchlist UI, jobs feed, push subscription |

---

## How a Job Goes From Posted to Notification

```
1. Scraper polls Greenhouse / Lever / Ashby every 60 seconds
2. For each job found:
   a. Check Redis — if key exists, skip (seen in last 24h)
   b. Check Postgres — if external_id exists, skip (permanent dedup)
   c. POST to api-service /internal/jobs — persists the job
   d. POST to notification-service /internal/notify — triggers push
3. Notification service queries api-service for push subscriptions
   of users who watchlisted that company
4. Sends Web Push to each subscriber via VAPID
5. Browser service worker receives push event, displays OS notification
```

---

## Deduplication — Three Layers

| Layer | Mechanism | Scope |
|---|---|---|
| Redis | Atomic SET NX with 24h TTL | Fast path — prevents DB load for recently seen jobs |
| Service check | `existsByExternalId()` before insert | Catches anything Redis misses after TTL expires |
| DB constraint | UNIQUE constraint on `external_id` | Final safety net — rejects duplicate inserts at the DB level |

---

## Data Model

**User** → has many **Watchlist** entries → each points to a **Company**
**Job** → belongs to a **Company**, identified by a namespaced `external_id` (e.g. `greenhouse-123456`)
**PushSubscription** → belongs to a User, stores browser push endpoint + VAPID encryption keys

---

## Company Discovery

When a user searches for a company not in the DB, the api-service treats the query as an ATS slug and tries Greenhouse, Lever, and Ashby in order. If found on any platform, the company is created in the DB and returned immediately. Confirmed misses are cached in an `unsupported_companies` table to avoid redundant ATS lookups on future searches.

---

## Infrastructure

- **Docker Compose** — all services run as containers, communicate by container hostname
- **AWS EC2 t3.micro** — 2 vCPU, 1GB RAM, free tier
- **Nginx** — reverse proxy handling HTTPS termination, routes traffic to frontend (port 3000) and api-service (port 8080)
- **Let's Encrypt** — free TLS certificate via Certbot, auto-renews
- **Elastic IP** — static IP so the URL stays stable across EC2 restarts
- **Postgres volume** — named Docker volume persists DB data across container restarts

## Known Tradeoffs

- **Kafka removed for deployment** — Kafka + Zookeeper require ~800MB RAM, nearly the entire free-tier instance. Replaced with direct HTTP calls. In production this would be AWS MSK.
- **Web Push requires HTTPS** — notifications only work on the deployed version, not localhost.
- **ATS slug matching** — company discovery requires the exact slug used by the ATS. "Figma" works; "Figma Inc" does not.
