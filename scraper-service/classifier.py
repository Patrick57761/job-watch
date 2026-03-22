import os
import json
from openai import OpenAI

SYSTEM_PROMPT = """You are a job classifier. Given a job title, return a JSON object with two fields:
- "category": one of ["technical", "product", "design", "marketing", "recruiting", "business", "leadership", "other"]
- "seniority": one of ["intern", "early career", "mid-level", "experienced", "any"]

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
- intern: intern, co-op, apprentice
- early career: new grad, associate, junior, level I, L3, E3
- mid-level: no level indicator, level II, L4, E4, SWE without qualifier
- experienced: senior, staff, principal, lead, manager, director, VP, L5+, E5+
- any: when seniority is genuinely ambiguous or role spans multiple levels

Respond with only valid JSON, no explanation."""

def classify_job(title: str) -> dict:
    try:
        client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))
        response = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[
                {"role": "system", "content": SYSTEM_PROMPT},
                {"role": "user", "content": title},
            ],
            response_format={"type": "json_object"},
            temperature=0,
        )
        result = json.loads(response.choices[0].message.content)
        category = result.get("category", "other")
        seniority = result.get("seniority", "any")
        return {"category": category, "seniority": seniority}
    except Exception as e:
        return {"category": "other", "seniority": "any"}
