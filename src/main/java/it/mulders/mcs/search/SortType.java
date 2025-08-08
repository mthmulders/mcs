package it.mulders.mcs.search;

/**
 * Sorting types for artifacts.
 * Thanks to <a href="https://stackoverflow.com/a/79687743/555366">mwalter on Stackoverflow</a>
 */
public enum SortType {
    VERSION_ASCENDING("v+asc"),
    VERSION_DESCENDING("v+desc");

    private final String sorting;

    SortType(String sorting) {
        this.sorting = sorting;
    }

    public String getSorting() {
        return sorting;
    }
}
