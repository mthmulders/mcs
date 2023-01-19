package it.mulders.mcs.search;

public sealed interface SearchQuery permits CoordinateQuery, ClassnameQuery, WildcardSearchQuery {
    int searchLimit();

    int start();

    String toSolrQuery();

    Builder<? extends SearchQuery> toBuilder();

    static SearchQuery.Builder<? extends SearchQuery> search(String query) {
        var isCoordinateSearch = query.contains(":");
        if (isCoordinateSearch) {
            var parts = query.split(":");
            return switch (parts.length) {
                case 1 -> new CoordinateQuery.Builder(parts[0], null);
                case 2 -> new CoordinateQuery.Builder(parts[0], parts[1]);
                case 3 -> new CoordinateQuery.Builder(parts[0], parts[1], parts[2]);
                default -> {
                    var msg = """
                            Searching a particular artifact requires at least groupId:artifactId and optionally :version
                            """;
                    throw new IllegalQueryException(msg);
                }
            };
        } else {
            return new WildcardSearchQuery.Builder(query);
        }
    }

    static ClassnameQuery.Builder classSearch(String query) {
        return new ClassnameQuery.Builder(query);
    }

    interface Builder<T extends SearchQuery> {
        Builder<T> withLimit(final Integer limit);

        Builder<T> withStart(final Integer start);

        SearchQuery build();
    }
}