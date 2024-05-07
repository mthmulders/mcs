package it.mulders.mcs.search;

import static it.mulders.mcs.search.Constants.DEFAULT_MAX_SEARCH_RESULTS;
import static it.mulders.mcs.search.Constants.DEFAULT_START;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public record WildcardSearchQuery(String term, int searchLimit, int start) implements SearchQuery {
  @Override
  public String toSolrQuery() {
    return String.format(
        "q=%s&start=%d&rows=%d",
        URLEncoder.encode(term, StandardCharsets.UTF_8), start(), searchLimit());
  }

  @Override
  public WildcardSearchQuery.Builder toBuilder() {
    return new WildcardSearchQuery.Builder(term()).withLimit(searchLimit()).withStart(start());
  }

  public static class Builder implements SearchQuery.Builder<WildcardSearchQuery> {
    private final String query;
    private Integer limit = DEFAULT_MAX_SEARCH_RESULTS;
    private Integer start = DEFAULT_START;

    public Builder(String query) {
      this.query = query;
    }

    @Override
    public Builder withStart(Integer start) {
      if (this.start != null) {
        this.start = start;
      }
      return this;
    }

    @Override
    public Builder withLimit(final Integer limit) {
      if (limit != null) {
        this.limit = limit;
      }
      return this;
    }

    @Override
    public SearchQuery build() {
      return new WildcardSearchQuery(query, limit, start);
    }
  }
}
