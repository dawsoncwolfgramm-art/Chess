package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import datamodel.*;


public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(String[] params) throws Exception {
        String username = params[0];
        String password = params[1];
        String email = params[2];
        var registerRequest = new UserData(username, password, email);
        var body = gson.toJson(registerRequest);
        var register = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/user"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();
        var response = client.send(register, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), AuthData.class);
        } else {
            throw new Exception(response.statusCode() + " " + response.body());
        }
    }

    public AuthData login(String[] params) throws Exception {
        String username = params[0];
        String password = params[1];
        var login = new LoginRequest(username, password);
        var body = gson.toJson(login);
        var loginRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();
        var response = client.send(loginRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), AuthData.class);
        } else {
            throw new Exception(response.statusCode() + " " + response.body());
        }
    }

    public void logout(String authToken) throws Exception {
        var logoutRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("authorization", authToken)
                .DELETE()
                .build();
        var response = client.send(logoutRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return;
        } else {
            throw new Exception(response.statusCode() + " " + response.body());
        }
    }

    public Integer createGame(String authToken, String gameName) throws Exception {
        var game = new GameData(1, null, null, gameName, null);
        var body = gson.toJson(game);
        var createGameRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("authorization", authToken)
                .build();
        var response = client.send(createGameRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            HashMap<String, Double> gameDataHash = gson.fromJson(response.body(), HashMap.class);
            Double gameIdDouble = gameDataHash.get("gameID");
            return gameIdDouble.intValue();
        } else {
            throw new Exception(response.statusCode() + " " + response.body());
        }
    }

    public List<GameData> listGames(String authToken) throws Exception {
        var listGamesRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("authorization", authToken)
                .GET()
                .build();
        var response = client.send(listGamesRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            var gameList = gson.fromJson(response.body(), ListGamesResponse.class);
            return gameList.getGames();
        } else {
            throw new Exception(response.statusCode() + " " + response.body());
        }
    }
}
