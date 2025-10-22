package dataaccess;


import chess.ChessGame;
import datamodel.GameData;
import datamodel.UserData;
import datamodel.AuthData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryDataAccess implements DataAccess {

    private HashMap<String, UserData> users = new HashMap<>();
    private HashMap<String, AuthData> auth = new HashMap<>();
    private HashMap<Integer, GameData> game = new HashMap<>();

    public void clear() {
        users.clear();
        auth.clear();
        game.clear();
    }

    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void addAuth(AuthData authData) {
        auth.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auth.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        auth.remove(authToken);
    }

    @Override
    public void addGame(GameData gameData) {
        game.put(gameData.gameID(), gameData);
    }

    @Override
    public List<GameData> getAllGames() {
        return new ArrayList<>(game.values());
    }

    @Override
    public GameData getGame(int gameId) {
        return game.get(gameId);
    }

    @Override
    public AuthData getPlayerName(String authToken) {
        return auth.get(authToken);
    }

    public void updateGame(int gameId, String whiteUsername, String blackUsername, String gameName) {
        game.put(gameId, new GameData(gameId, whiteUsername, blackUsername, gameName, null));
    }

}
