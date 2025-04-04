package org.example.model.api;


import org.example.model.request.BuildRequest;
import org.example.model.response.towerResponse.TowersResponse;
import org.example.model.response.WordsResponse;

public interface DatsCityApi {
    WordsResponse getWords();
    WordsResponse shuffleWords();
    TowersResponse getTowers();
    WordsResponse buildTower(BuildRequest request);
}