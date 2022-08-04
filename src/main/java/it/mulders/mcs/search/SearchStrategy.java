package it.mulders.mcs.search;

import it.mulders.mcs.common.Result;

public interface SearchStrategy {
    Result<SearchResult> execute(final SearchQuery query);
}
