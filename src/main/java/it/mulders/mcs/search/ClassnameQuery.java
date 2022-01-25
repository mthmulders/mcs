package it.mulders.mcs.search;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static it.mulders.mcs.search.Constants.DEFAULT_MAX_SEARCH_RESULTS;

public record ClassnameQuery(
        String query,
        boolean fullyQualified,
        int searchLimit
) implements SearchQuery {
    @Override
    public String toSolrQuery() {
        if (fullyQualified) {
            return String.format("q=fc:%s&start=%d&rows=%d",
                    URLEncoder.encode(query, StandardCharsets.UTF_8), 0, searchLimit());
        } else {
            return String.format("q=c:%s&start=%d&rows=%d",
                    URLEncoder.encode(query, StandardCharsets.UTF_8), 0, searchLimit());
        }
    }

    public static class Builder implements SearchQuery.Builder {
        private String query;
        private Integer limit = DEFAULT_MAX_SEARCH_RESULTS;
        private boolean fullyQualified = false;

        public Builder(String query) {
            this.query = query;
        }

        public Builder isFullyQualified(boolean isFullyQualified) {
            this.fullyQualified = isFullyQualified;
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
            return new ClassnameQuery(query, fullyQualified, limit);
        }
    }

}
