package org.example;

import org.example.model.api.ApiClient;
import org.example.model.api.DatsCityApi;
import org.example.model.script.TowerBuilder;

public class Main {
    // Флаг для переключения между серверами (true - тестовый, false - релизный)
    private static final boolean USE_TEST_SERVER = true;
    private static final String token = "e2dcd786-b907-4984-97cd-5c34bf8edbd7";

    public static void main(String[] args) {
        // Выбираем сервер и токен в зависимости от флага
        String baseUrl = USE_TEST_SERVER
                ? "https://games-test.datsteam.dev"
                : "https://games.datsteam.dev";




        DatsCityApi api = new ApiClient(baseUrl, token);
        TowerBuilder builder = new TowerBuilder(api);

        System.out.println("Игра запущена на сервере: " + baseUrl);

        while (true) {
            builder.playTurn();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}