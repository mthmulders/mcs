package it.mulders.mcs.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record SearchResult(
        List<Artifact> artifacts
) {
    SearchResult append(final SearchResult other) {
        var newArtifacts = new ArrayList<>(artifacts());
        newArtifacts.addAll(other.artifacts);

        return new SearchResult(newArtifacts);

    }

    static SearchResult fromSearchResponse(final SearchResponse response) {
        var artifacts = Arrays.stream(response.response().docs())
                .map(d -> new Artifact(d.id(), d.g(), d.a(), d.v()))
                .collect(Collectors.toList());

        return new SearchResult(artifacts);
    }

    static record Artifact(
            String id,
            String groupId,
            String artifactId,
            String version
    ) {

    }
}
