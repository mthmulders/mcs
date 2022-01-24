package it.mulders.mcs.search;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static it.mulders.mcs.search.Constants.DEFAULT_MAX_SEARCH_RESULTS;

public record CoordinateQuery (
        String groupId,
        String artifactId,
        String version,
        Integer searchLimit
) implements SearchQuery {
    @Override
    public String toSolrQuery() {
        String query;
        if (version == null) {
            query = String.format("g:%s AND a:%s", groupId, artifactId);
        } else {
            query = String.format("g:%s AND a:%s AND v:%s", groupId, artifactId, version);
        }

        return String.format("q=%s&core=gav&start=%d&rows=%d",
                URLEncoder.encode(query, StandardCharsets.UTF_8), 0, searchLimit());
    }

    public static class Builder implements SearchQuery.Builder {
        private String groupId;
        private String artifactId;
        private String version;
        private Integer limit = DEFAULT_MAX_SEARCH_RESULTS;

        public Builder(String groupId, String artifactId) {
            this(groupId, artifactId, null);
        }

        public Builder(String groupId, String artifactId, String version) {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
        }

        @Override
        public Builder withLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        @Override
        public SearchQuery build() {
            return new CoordinateQuery(groupId, artifactId, version, limit);
        }
    }
}
