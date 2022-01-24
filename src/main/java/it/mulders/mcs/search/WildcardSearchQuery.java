package it.mulders.mcs.search;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static it.mulders.mcs.search.Constants.DEFAULT_MAX_SEARCH_RESULTS;

public record WildcardSearchQuery(
        String term,
        Integer searchLimit
) implements SearchQuery {
    @Override
    public String toSolrQuery() {
        return String.format("q=%s&start=%d&rows=%d",
                URLEncoder.encode(term, StandardCharsets.UTF_8), 0, searchLimit());
    }

    public static class Builder implements SearchQuery.Builder {
        private String query;
        private Integer limit = DEFAULT_MAX_SEARCH_RESULTS;

        public Builder(String query) {
            this.query = query;
        }

        @Override
        public Builder withLimit(final Integer limit) {
            if (limit != null) {
                this.limit = limit;
            }
            return this;
        }

        @Override
        public SearchQuery build() {
            return new WildcardSearchQuery(query, limit);
        }
    }
}
