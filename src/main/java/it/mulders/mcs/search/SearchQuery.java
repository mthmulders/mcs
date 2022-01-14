package it.mulders.mcs.search;

public sealed interface SearchQuery permits ClassnameQuery, CoordinateQuery, FullClassnameQuery, WildcardSearchQuery {
    static SearchQueryBuilder search(String query) {
        return new SearchQueryBuilder(false)
                .withQuery(query);
    }

    static SearchQueryBuilder classSearch(String query) {
        return new SearchQueryBuilder(true)
                .withQuery(query);
    }

    int searchLimit();

    class SearchQueryBuilder {
        private String query;
        private boolean fullName;
        private Integer limit;
        private final boolean isClassSearch;

        private SearchQueryBuilder(boolean isClassSearch) {
            this.isClassSearch = isClassSearch;
        }

        private SearchQueryBuilder withQuery(String query) {
            this.query = query;
            return this;
        }

        public SearchQueryBuilder withLimit(final Integer limit) {
            this.limit = limit;
            return this;
        }

        public SearchQueryBuilder withFullName(final Boolean fullName) {
            this.fullName = fullName != null && fullName;
            return this;
        }

        public SearchQuery build() {
            if (isClassSearch) {
                if (fullName) {
                    return new FullClassnameQuery(query, limit != null ? limit : Constants.DEFAULT_MAX_SEARCH_RESULTS);
                } else {
                    return new ClassnameQuery(query, limit != null ? limit : Constants.DEFAULT_MAX_SEARCH_RESULTS);
                }
            } else {
                if (isCoordinateSearch(query)) {
                    var parts = query.split(":");
                    if (parts.length < 2 || parts.length > 3) {
                        var msg = """
                        Searching a particular artifact requires at least groupId:artifactId and optionally :version
                        """;
                        throw new IllegalArgumentException(msg);
                    }

                    var groupId = parts[0];
                    var artifactId = parts[1];
                    var version = parts.length == 3 ? parts[2] : null;
                    return new CoordinateQuery(groupId, artifactId, version, limit != null ? limit : Constants.DEFAULT_MAX_SEARCH_RESULTS);
                } else {
                    return new WildcardSearchQuery(query, limit != null ? limit : 1);
                }
            }
        }
    }

    String toSolrQuery();

    private static boolean isCoordinateSearch(final String query) {
        return query.contains(":");
    }
}