package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONException;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        if (breed == null || breed.isBlank()) {
            throw new BreedNotFoundException("Breed cannot be null or blank.");
        }

        String url = "https://dog.ceo/api/breed/"
                + breed.trim().toLowerCase(Locale.ROOT)
                + "/list";

        Request request = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                throw new BreedNotFoundException("Empty response from API.");
            }
            String body = response.body().string();

            JSONObject json = new JSONObject(body);
            String status = json.optString("status", "");

            if (!"success".equals(status)) {
                String msg = json.optString("message", "Breed not found (main breed does not exist)");
                throw new BreedNotFoundException(msg);
            }

            JSONArray arr = json.getJSONArray("message");
            List<String> out = new ArrayList<>(arr.length());
            for (int i = 0; i < arr.length(); i++) {
                out.add(arr.getString(i));
            }
            return out;

        } catch (IOException | JSONException e) {
            throw new BreedNotFoundException("Could not fetch breed list from API.");
        }
    }
}