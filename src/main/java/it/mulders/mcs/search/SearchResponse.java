package it.mulders.mcs.search;

import it.mulders.mcs.search.vulnerability.ComponentReportResponse.ComponentReport;

public record SearchResponse(Object responseHeader, Response response) {
  public record Response(int numFound, int start, Doc[] docs) {
    public record Doc(
        String id,
        String g,
        String a,
        String v,
        String latestVersion,
        String p,
        long timestamp,
        ComponentReport componentReport) {
      public Doc(
          String id, String g, String a, String v, String latestVersion, String p, long timestamp) {
        this(id, g, a, v, latestVersion, p, timestamp, null);
      }

      public Doc withComponentReport(ComponentReport componentReport) {
        return new Doc(id(), g(), a(), v(), latestVersion(), p(), timestamp(), componentReport);
      }
    }
  }
}
