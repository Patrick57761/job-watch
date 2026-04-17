import os
import json
from openai import OpenAI

SYSTEM_PROMPT = """You are a job classifier. Given a job title and location, return a JSON object with four fields:
- "category": one of ["technical", "product", "design", "marketing", "recruiting", "business", "leadership", "other"]
- "seniority": one of ["intern", "early career", "mid-level", "experienced", "any"]
- "isUS": true if the job is in the United States or is remote but restricted to US candidates, false otherwise
- "isRemote": true if the job can be done fully remotely, false otherwise

Guidelines for category:
- technical: software engineers, infrastructure, security, ML engineers, DevOps, data engineers, data scientists
- product: product management, program management, technical program manager
- design: UX, UI, product design, brand, creative
- marketing: marketing, growth, content, communications, PR
- recruiting: recruiter, talent acquisition, sourcer
- business: sales, finance, operations, legal, HR, account management, customer success
- leadership: director, VP, head of, C-level, general manager
- other: anything that doesn't clearly fit

Guidelines for seniority:
- intern: intern, co-op, apprentice, or any role with a season+year suffix like "(Summer 2026)", "(Fall 2025)", "(Spring 2026)" — NOTE: "internals" in a title (e.g. "Database Engine Internals") refers to a technical domain, not seniority
- early career: new grad, associate, junior, level I, L3, E3
- mid-level: no level indicator, level II, L4, E4, SWE without qualifier
- experienced: senior, staff, principal, lead, manager, director, VP, L5+, E5+
- any: when seniority is genuinely ambiguous or role spans multiple levels

Guidelines for isUS:
- true: location contains a US city, state, or "United States"; or location says "Remote" with no country specified (assume US-based company)
- true: location says "Remote, US" or "US Remote" or similar
- false: location explicitly names a non-US country (UK, Canada, Germany, etc.)
- false: location says "Worldwide" or "Global"

Guidelines for isRemote:
- true: location contains "remote" or "work from home" or "WFH"
- false: location is a specific city/office with no remote mention
- false: "hybrid" alone does not count as remote

Respond with only valid JSON, no explanation."""

VALID_CATEGORIES = {"technical", "product", "design", "marketing", "recruiting", "business", "leadership", "other"}
VALID_SENIORITIES = {"intern", "early career", "mid-level", "experienced", "any"}

def classify_job(title: str, location: str) -> dict:
    try:
        client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))
        response = client.chat.completions.create(
            model="gpt-5.4-nano",
            messages=[
                {"role": "system", "content": SYSTEM_PROMPT},
                {"role": "user", "content": f"Title: {title}\nLocation: {location}"},
            ],
            response_format={"type": "json_object"},
            temperature=0,
        )
        result = json.loads(response.choices[0].message.content)
        category = result.get("category", "other")
        seniority = result.get("seniority", "any")
        if category not in VALID_CATEGORIES:
            category = "other"
        if seniority not in VALID_SENIORITIES:
            seniority = "any"
        return {
            "category": category,
            "seniority": seniority,
            "isUS": bool(result.get("isUS", False)),
            "isRemote": bool(result.get("isRemote", False)),
        }
    except Exception as e:
        return {"category": "other", "seniority": "any", "isUS": False, "isRemote": False}
