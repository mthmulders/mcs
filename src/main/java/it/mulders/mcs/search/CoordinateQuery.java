package it.mulders.mcs.search;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import static it.mulders.mcs.search.Constants.DEFAULT_MAX_SEARCH_RESULTS;
import static it.mulders.mcs.search.Constants.DEFAULT_START;

public record CoordinateQuery(
        String groupId,
        String artifactId,
        String version,
        int searchLimit,
        int start
) implements SearchQuery {
    @Override
    public String toSolrQuery() {
        final List<String> parts = new LinkedList<>();
        if (!groupId.isBlank()) parts.add("g:%s".formatted(groupId));
        if (!artifactId.isBlank()) parts.add("a:%s".formatted(artifactId));
        if (!version.isBlank()) parts.add("v:%s".formatted(version));
        var query = String.join(" AND ", parts);

        return String.format("q=%s&core=gav&start=%d&rows=%d",
                URLEncoder.encode(query, StandardCharsets.UTF_8), start(), searchLimit());
    }

    @Override
    public CoordinateQuery.Builder toBuilder() {
        return new CoordinateQuery.Builder(groupId(), artifactId(), version())
                .withLimit(searchLimit())
                .withStart(start());
    }

    public static class Builder implements SearchQuery.Builder<CoordinateQuery> {
        private final String groupId;
        private final String artifactId;
        private final String version;
        private Integer limit = DEFAULT_MAX_SEARCH_RESULTS;
        private Integer start = DEFAULT_START;

        public Builder(String groupId, String artifactId) {
            this(groupId, artifactId, null);
        }

        public Builder(String groupId, String artifactId, String version) {
            this.groupId = sanitise(groupId);
            this.artifactId = sanitise(artifactId);
            this.version = sanitise(version);
        }

        private String sanitise(String input) {
            return input == null ? "" : input;
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
            return new CoordinateQuery(groupId, artifactId, version, limit, start);
        }
    }
}
