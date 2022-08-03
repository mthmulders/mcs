package it.mulders.mcs.search;

public record SearchResponse(Header header, Response response) {
    public record Header(Params params) {
        public record Params(String q, int start, String sort, int rows) {
        }
    }
    public record Response(int numFound, int start, Doc[] docs) {
        public record Doc(String id, String g, String a, String v, String latestVersion, String p, long timestamp) {
        }
    }
}
