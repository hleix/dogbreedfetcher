package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private int callsMade = 0;
    private final BreedFetcher fetcher;
    private final Map<String, List<String>> cache = new HashMap<>();

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = Objects.requireNonNull(fetcher);
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String key = breed.trim().toLowerCase(Locale.ROOT);

        // Check the cache first
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        // Otherwise, call the underlying fetcher
        callsMade++;
        List<String> result = fetcher.getSubBreeds(breed);

        // Cache only successful results (don’t cache exceptions)
        cache.put(key, Collections.unmodifiableList(new ArrayList<>(result)));
        return result;
    }

    public int getCallsMade() {
        return callsMade;
    }
}