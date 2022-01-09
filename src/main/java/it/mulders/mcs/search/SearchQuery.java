package it.mulders.mcs.search;

public sealed interface SearchQuery permits CoordinateQuery, WildcardSearchQuery {
    Integer searchLimit();

    String toSolrQuery();

    static SearchQuery fromUserInput(final String input) {
        if (isCoordinateSearch(input)) {
            var parts = input.split(":");
            if (parts.length < 2 || parts.length > 3) {
                var msg = """
                        Searching a particular artifact requires at least groupId:artifactId and optionally :version
                        """;
                throw new IllegalArgumentException(msg);
            }

            var groupId = parts[0];
            var artifactId = parts[1];
            var hasVersion = parts.length == 3;
            if (hasVersion) {
                var version = parts[2];
                return new CoordinateQuery(groupId, artifactId, version);
            } else {
                return new CoordinateQuery(groupId, artifactId);
            }
        } else {
            return new WildcardSearchQuery(input);
        }
    }

    private static boolean isCoordinateSearch(final String query) {
        return query.contains(":");
    }
}