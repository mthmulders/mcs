package it.mulders.mcs.search.artifact;

import static it.mulders.mcs.Constants.DEFAULT_MAX_SEARCH_RESULTS;
import static it.mulders.mcs.Constants.DEFAULT_START;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public record ClassnameQuery(String query, boolean fullyQualified, int searchLimit, int start) implements SearchQuery {
    @Override
    public String toSolrQuery() {
        if (fullyQualified) {
            return "q=fc:%s&start=%d&rows=%d"
                    .formatted(URLEncoder.encode(query, StandardCharsets.UTF_8), start(), searchLimit());
        } else {
            return "q=c:%s&start=%d&rows=%d"
                    .formatted(URLEncoder.encode(query, StandardCharsets.UTF_8), start(), searchLimit());
        }
    }

    @Override
    public ClassnameQuery.Builder toBuilder() {
        return new ClassnameQuery.Builder(query())
                .isFullyQualified(fullyQualified())
                .withLimit(searchLimit())
                .withStart(start());
    }

    public static class Builder implements SearchQuery.Builder<ClassnameQuery> {
        private final String query;
        private Integer limit = DEFAULT_MAX_SEARCH_RESULTS;
        private Integer start = DEFAULT_START;
        private boolean fullyQualified = false;

        public Builder(String query) {
            this.query = query;
        }

        public Builder isFullyQualified(boolean isFullyQualified) {
            this.fullyQualified = isFullyQualified;
            return this;
        }

        @Override
        public Builder withStart(Integer start) {
            if (this.start != null) {
                this.start = start;
            }
            return this;
        }

        @Override
        public Builder withLimit(Integer limit) {
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
