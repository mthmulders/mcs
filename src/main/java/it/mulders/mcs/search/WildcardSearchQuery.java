package it.mulders.mcs.search;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static it.mulders.mcs.search.Constants.DEFAULT_MAX_SEARCH_RESULTS;

public record WildcardSearchQuery(
        String term,
        Integer searchLimit
) implements SearchQuery {
    public WildcardSearchQuery(final String term) {
        this(term, DEFAULT_MAX_SEARCH_RESULTS);
    }

    @Override
    public SearchQuery withLimit(final Integer limit) {
        if (limit != null) {
            return new WildcardSearchQuery(term, limit);
        } else {
            return this;
        }
    }

    @Override
    public String toSolrQuery() {
        return String.format("q=%s&start=%d&rows=%d",
                URLEncoder.encode(term, StandardCharsets.UTF_8), 0, searchLimit());
    }
}
