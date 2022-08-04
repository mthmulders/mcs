package it.mulders.mcs.search;

import it.mulders.mcs.common.Result;

public class DefaultStrategy implements SearchStrategy {
    private final SearchClient client;
    private final SearchStrategyCalculator strategyCalculator;

    public DefaultStrategy(
            final SearchClient client,
            final SearchStrategyCalculator strategyCalculator
    ) {
        this.client = client;
        this.strategyCalculator = strategyCalculator;
    }

    @Override
    public Result<SearchResult> execute(final SearchQuery query) {
        return client.search(query)
                .map(response -> strategyCalculator.select(query, response))
                .flatMap(strategy -> strategy.execute(query));
    }
}
