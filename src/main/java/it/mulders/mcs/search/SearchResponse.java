package it.mulders.mcs.search;

public record SearchResponse(Object responseHeader, Response response) {
    public record Response(int numFound, int start, Doc[] docs) {
        public record Doc(String id, String g, String a, String latestVersion, String p, long timestamp) {
        }
    }
}
