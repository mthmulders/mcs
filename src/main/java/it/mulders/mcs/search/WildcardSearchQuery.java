package it.mulders.mcs.search;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static it.mulders.mcs.search.Constants.DEFAULT_MAX_SEARCH_RESULTS;

public record WildcardSearchQuery(
        String term,
        int searchLimit
) implements SearchQuery {

    @Override
    public String toSolrQuery() {
        return String.format("q=%s&start=%d&rows=%d",
                URLEncoder.encode(term, StandardCharsets.UTF_8), 0, searchLimit());
    }
}
