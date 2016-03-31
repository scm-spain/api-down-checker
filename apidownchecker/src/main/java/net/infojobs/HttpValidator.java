package net.infojobs;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpValidator implements ApiValidator {

    private final OkHttpClient httpClient;

    private final String endpoint;

    public HttpValidator(OkHttpClient httpClient, String endpoint) {
        this.httpClient = httpClient;
        this.endpoint = endpoint;
    }

    @Override
    public boolean isOk() {
        Request request = new Request.Builder()
                .url(endpoint)
                .build();
        try {
            Response response = httpClient.newCall(request).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }

    public String getEndpoint() {
        return endpoint;
    }
}
