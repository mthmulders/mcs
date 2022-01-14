package it.mulders.mcs.search;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public record ClassnameQuery(
        String query,
        int searchLimit
) implements SearchQuery {
    @Override
    public String toSolrQuery() {
        return String.format("q=c:%s&start=%d&rows=%d",
                URLEncoder.encode(query, StandardCharsets.UTF_8), 0, searchLimit());
    }
}
