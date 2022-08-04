package it.mulders.mcs.search;

import it.mulders.mcs.common.Result;

public class ModifiedQueryStrategy extends DefaultStrategy implements SearchStrategy {
    private final SearchQuery modifiedSearchQuery;

    public ModifiedQueryStrategy(
            final SearchClient client,
            final SearchQuery modifiedSearchQuery,
            final SearchStrategyCalculator strategyCalculator
    ) {
        super(client, strategyCalculator);
        this.modifiedSearchQuery = modifiedSearchQuery;
    }

    @Override
    public Result<SearchResult> execute(SearchQuery query) {
        return super.execute(modifiedSearchQuery);
    }
}
