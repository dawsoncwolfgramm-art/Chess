package service;

import chess.ChessGame;
import datamodel.AuthData;
import dataaccess.DataAccess;
import datamodel.GameData;
import datamodel.UserData;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;
    private List<Integer> gamesIds = new ArrayList();

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() {
        dataAccess.clear();
    }


    public AuthData register(UserData user) throws Exception {
        if (user == null || user.username() == null || user.username().isBlank() ||
                user.email() == null || user.email().isBlank() ||
                user.password() == null || user.password().isBlank()) {
            throw new Exception("bad request");
        }
        if (dataAccess.getUser(user.username()) != null) {
            throw new Exception("already taken");
        }
        dataAccess.createUser(user);
        AuthData authData = new AuthData(user.username(), generateAuthToken());
        dataAccess.addAuth(authData);
        return authData;
    }

    public AuthData login(UserData user) throws Exception {
        if (user == null || user.username() == null || user.username().isBlank() ||
                user.password() == null || user.password().isBlank()) {
            throw new Exception("bad request");
        }
        if (dataAccess.getUser(user.username()) == null) {
            throw new Exception("unauthorized");
        }
        UserData userData = dataAccess.getUser(user.username());
        if (!user.password().equals(userData.password())) {
            throw new Exception("unauthorized");
        }
        AuthData authData = new AuthData(user.username(), generateAuthToken());
        dataAccess.addAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws Exception {
        if (authToken == null || authToken.isBlank()) {
            throw new Exception("unauthorized");
        }

        if (dataAccess.getAuth(authToken) == null) {
            throw new Exception("unauthorized");
        }
        dataAccess.deleteAuth(authToken);
    }

    //use the script they gave you to generate the authToken
    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    public Integer createGame(String authToken, GameData userGameData) throws Exception {
        if (authToken == null || authToken.isBlank()) {
            throw new Exception("bad request");
        }
        if (dataAccess.getAuth(authToken) == null) {
            throw new Exception("unauthorized");
        }
        if (userGameData.gameName() == null) {
            throw new Exception("bad request");
        }


        GameData gameData;
        int num = 1;
        while (true) {
            if (!gamesIds.contains(num)) {
                gamesIds.add(num);
                gameData = new GameData(num, null,
                        null, userGameData.gameName(), null);
                dataAccess.addGame(gameData);
                break;
            }
            num++;
        }
        return gameData.gameID();
    }
}
