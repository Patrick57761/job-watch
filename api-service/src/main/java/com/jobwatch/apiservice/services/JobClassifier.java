package com.jobwatch.apiservice.services;

public class JobClassifier {

    private static boolean hasWord(String text, String word) {
        return text.matches(".*\\b" + word + "\\b.*");
    }

    public static String classifyCategory(String title) {
        if (title == null) return "other";
        String t = title.toLowerCase();

        if (t.contains("product manager") || t.startsWith("pm ") || t.equals("pm") ||
            t.contains("program manager") || t.contains("product management")) {
            return "product";
        }
        if (t.contains("engineer") || t.contains("developer") || t.contains("software") ||
            t.contains("data") || t.contains("machine learning") || t.contains("computer vision") ||
            hasWord(t, "ml") || hasWord(t, "ai") || t.contains("artificial intelligence") ||
            t.contains("infrastructure") || t.contains("security") || t.contains("devops") ||
            t.contains("sre") || t.contains("backend") || t.contains("frontend") ||
            t.contains("fullstack") || t.contains("full stack") || t.contains("scientist") ||
            t.contains("analyst") || t.contains("research") || t.contains("r&d") ||
            t.contains("qa") || t.contains("test") || t.contains("mobile") || t.contains("ios") ||
            t.contains("android") || t.contains("cloud") || t.contains("platform") ||
            t.contains("systems") || t.contains("network") || t.contains("database")) {
            return "technical";
        }
        if (t.contains("design") || t.contains("ux") || t.contains("ui ") ||
            t.contains("creative") || t.contains("visual")) {
            return "design";
        }
        if (t.contains("market") || t.contains("growth") || t.contains("brand") ||
            t.contains("content") || t.contains("seo") || t.contains("communications") ||
            t.contains("social media") || t.contains("copywriter")) {
            return "marketing";
        }
        if (t.contains("recruit") || t.contains("talent") || t.contains("hr ") ||
            t.contains("human resources") || t.contains("people partner")) {
            return "recruiting";
        }
        if (t.contains("director") || t.contains("vice president") || t.contains("vp ") ||
            t.contains("head of") || t.contains("chief") || t.contains("cto") ||
            t.contains("ceo") || t.contains("coo") || t.contains("cfo")) {
            return "leadership";
        }
        if (t.contains("operations") || t.contains("strategy") || t.contains("finance") ||
            t.contains("legal") || t.contains("accounting") || t.contains("sales") ||
            t.contains("business development") || t.contains("account manager") ||
            t.contains("account executive") || t.contains("biz dev")) {
            return "business";
        }
        return "other";
    }

    public static String classifySeniority(String title) {
        if (title == null) return "any";
        String t = title.toLowerCase();

        if (hasWord(t, "intern") || hasWord(t, "internship") || hasWord(t, "co-op") || hasWord(t, "coop")) {
            return "intern";
        }
        if (t.contains("senior") || t.contains("staff") || t.contains("principal") ||
            t.contains("lead") || t.contains("manager") || t.contains("director") ||
            t.contains("vp ") || t.contains("vice president") || t.contains("head of") ||
            t.contains("chief")) {
            return "experienced";
        }
        if (t.contains("new grad") || t.contains("entry") || t.contains("junior") ||
            t.contains("associate") || t.contains("early career")) {
            return "early career";
        }
        return "any";
    }
}
