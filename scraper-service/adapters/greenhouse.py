import requests

BASE_URL = "https://boards-api.greenhouse.io/v1/boards/{slug}/jobs"

def fetch_jobs(company_slug: str) -> list[dict]:
    url = BASE_URL.format(slug=company_slug)
    response = requests.get(url, timeout=10)
    response.raise_for_status()

    jobs = response.json().get("jobs", [])

    return [
        {
            "id": f"greenhouse-{job['id']}",
            "title": job["title"],
            "location": job.get("location", {}).get("name", "Unknown"),
            "url": job["absolute_url"],
            "updated_at": job["updated_at"],
            "platform": "greenhouse",
            "company_slug": company_slug,
        }
        for job in jobs
    ]
