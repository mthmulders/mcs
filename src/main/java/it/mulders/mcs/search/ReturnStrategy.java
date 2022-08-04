package it.mulders.mcs.search;

import it.mulders.mcs.common.Result;

public class ReturnStrategy implements SearchStrategy {
    private final SearchResult searchResult;

    public ReturnStrategy(final SearchResult searchResult) {
        this.searchResult = searchResult;
    }

    @Override
    public Result<SearchResult> execute(final SearchQuery query) {
        return new Result.Success<>(searchResult);
    }
}
