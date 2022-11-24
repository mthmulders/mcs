package it.mulders.mcs.search;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static it.mulders.mcs.search.Constants.DEFAULT_MAX_SEARCH_RESULTS;
import static it.mulders.mcs.search.Constants.DEFAULT_START;

public record WildcardSearchQuery(
        String term,
        int searchLimit,
        int start
) implements SearchQuery {
    @Override
    public String toSolrQuery() {
        return String.format("q=%s&start=%d&rows=%d",
                URLEncoder.encode(term, StandardCharsets.UTF_8), start(), searchLimit());
    }

    @Override
    public WildcardSearchQuery.BasicBuilder toBuilder() {
        return new BasicBuilder(term())
                .withLimit(searchLimit())
                .withStart(start());
    }

    public static class BasicBuilder implements SearchQuery.BasicBuilder {
        private final String query;
        private Integer limit = DEFAULT_MAX_SEARCH_RESULTS;
        private Integer start = DEFAULT_START;

        public BasicBuilder(String query) {
            this.query = query;
        }

        @Override
        public BasicBuilder withStart(Integer start) {
            if (this.start != null) {
                this.start = start;
            }
            return this;
        }

        @Override
        public BasicBuilder withLimit(final Integer limit) {
            if (limit != null) {
                this.limit = limit;
            }
            return this;
        }

        @Override
        public SearchQuery build() {
            return new WildcardSearchQuery(query, limit, start);
        }
    }
}
