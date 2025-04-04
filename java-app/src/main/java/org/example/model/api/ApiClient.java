package org.example.model.api;


import com.google.gson.Gson;
import org.example.model.request.BuildRequest;
import org.example.model.response.towerResponse.TowersResponse;
import org.example.model.response.WordsResponse;

import java.net.URI;
import java.net.http.*;

public class ApiClient implements DatsCityApi {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String baseUrl;
    private final String token;

    public ApiClient(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;
    }

    @Override
    public WordsResponse getWords() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/words"))
                .header("X-Auth-Token", token)
                .GET()
                .build();
        return sendRequest(request, WordsResponse.class);
    }

    @Override
    public WordsResponse shuffleWords() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/shuffle"))
                .header("X-Auth-Token", token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return sendRequest(request, WordsResponse.class);
    }

    @Override
    public TowersResponse getTowers() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/towers"))
                .header("X-Auth-Token", token)
                .GET()
                .build();
        return sendRequest(request, TowersResponse.class);
    }

    @Override
    public WordsResponse buildTower(BuildRequest request) {
        String json = gson.toJson(request);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/build"))
                .header("X-Auth-Token", token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return sendRequest(httpRequest, WordsResponse.class);
    }

    private <T> T sendRequest(HttpRequest request, Class<T> responseType) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return gson.fromJson(response.body(), responseType);
        } catch (Exception e) {
            throw new RuntimeException("API error: " + e.getMessage());
        }
    }
}