package information;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Ergast {
    private static final OkHttpClient client = new OkHttpClient();

    public String getData(String urlRequest) throws Exception {
        String url = "https://ergast.com/api/f1/" + urlRequest + ".json";

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                throw new RuntimeException("Erro: " + response.code());
            }
        }
    }

}
