import time
import logging
from adapters.greenhouse import fetch_jobs
from redis_client import is_new_job
from kafka_client import publish_job

logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")

POLL_INTERVAL_SECONDS = 60

COMPANIES = [
    {"slug": "stripe", "platform": "greenhouse"},
    {"slug": "airbnb", "platform": "greenhouse"},
    {"slug": "reddit", "platform": "greenhouse"},
]

def scrape_all():
    for company in COMPANIES:
        slug = company["slug"]
        logging.info(f"Scraping {slug}...")
        try:
            jobs = fetch_jobs(slug)
            new_count = 0
            for job in jobs:
                if is_new_job(job["id"]):
                    publish_job(job)
                    new_count += 1
            logging.info(f"{slug}: {new_count} new jobs published out of {len(jobs)} total")
        except Exception as e:
            logging.error(f"Failed to scrape {slug}: {e}")

if __name__ == "__main__":
    logging.info("Scraper started")
    while True:
        scrape_all()
        logging.info(f"Sleeping {POLL_INTERVAL_SECONDS}s...")
        time.sleep(POLL_INTERVAL_SECONDS)
