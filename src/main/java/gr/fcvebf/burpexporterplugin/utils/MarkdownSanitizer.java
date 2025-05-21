package gr.fcvebf.burpexporterplugin.utils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.ArrayList;
import java.util.List;

public class MarkdownSanitizer {

    public static List<String> sanitizeIssueDetails(List<String> rawDetails) {
        List<String> sanitized = new ArrayList<>();

        if (rawDetails != null) {
            for (String detail : rawDetails) {
                if (detail != null) {
                    // Clean HTML using JSoup (keep basic formatting if desired)
                    String safe = Jsoup.clean(detail, Safelist.basic());
                    sanitized.add(safe);
                }
            }
        }

        return sanitized;
    }
}