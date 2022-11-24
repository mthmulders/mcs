package it.mulders.mcs.search;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static it.mulders.mcs.search.Constants.DEFAULT_MAX_SEARCH_RESULTS;
import static it.mulders.mcs.search.Constants.DEFAULT_START;

public record ClassnameQuery(
        String query,
        boolean fullyQualified,
        int searchLimit,
        int start
) implements SearchQuery {
    @Override
    public String toSolrQuery() {
        if (fullyQualified) {
            return String.format("q=fc:%s&start=%d&rows=%d",
                    URLEncoder.encode(query, StandardCharsets.UTF_8), start(), searchLimit());
        } else {
            return String.format("q=c:%s&start=%d&rows=%d",
                    URLEncoder.encode(query, StandardCharsets.UTF_8), start(), searchLimit());
        }
    }

    @Override
    public ClassnameQuery.BasicBuilder toBuilder() {
        return new ClassnameQuery.BasicBuilder(query())
                .isFullyQualified(fullyQualified())
                .withLimit(searchLimit())
                .withStart(start());
    }

    public static class BasicBuilder implements SearchQuery.BasicBuilder {
        private final String query;
        private Integer limit = DEFAULT_MAX_SEARCH_RESULTS;
        private Integer start = DEFAULT_START;
        private boolean fullyQualified = false;

        public BasicBuilder(String query) {
            this.query = query;
        }

        public BasicBuilder isFullyQualified(boolean isFullyQualified) {
            this.fullyQualified = isFullyQualified;
            return this;
        }

        @Override
        public BasicBuilder withStart(Integer start) {
            if (this.start != null) {
                this.start = start;
            }
            return this;
        }

        @Override
        public BasicBuilder withLimit(Integer limit) {
            if (limit != null) {
                this.limit = limit;
            }
            return this;
        }

        @Override
        public SearchQuery build() {
            return new ClassnameQuery(query, fullyQualified, limit, start);
        }
    }

}
