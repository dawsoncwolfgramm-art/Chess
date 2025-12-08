package dataaccess;

import chess.ChessGame;
import datamodel.GameData;
import datamodel.UserData;
import datamodel.AuthData;

import java.util.*;

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
    public Optional<UserData> getUser(String username) {
        return Optional.ofNullable(users.get(username));
    }

    @Override
    public void addAuth(AuthData authData) {
        auth.put(authData.authToken(), authData);
    }

    @Override
    public Optional<AuthData> getAuth(String authToken) {
        return Optional.ofNullable(auth.get(authToken));
    }

    @Override
    public void deleteAuth(String authToken) {
        auth.remove(authToken);
    }

    @Override
    public Integer addGame(GameData gameData) {
        game.put(gameData.gameID(), gameData);
        return gameData.gameID();
    }

    @Override
    public List<GameData> getAllGames() {
        return new ArrayList<>(game.values());
    }

    @Override
    public Optional<GameData> getGame(int gameId) {
        return Optional.ofNullable(game.get(gameId));
    }

    public void updateGame(int gameId, String whiteUsername, String blackUsername, String gameName) {
        game.put(gameId, new GameData(gameId, whiteUsername, blackUsername, gameName, null));
    }


    public void updateGameState(int gameId, ChessGame chessGame) throws Exception {
        GameData old = game.get(gameId);  // however you store them
        if (old == null) {
            throw new DataAccessException("game not found");
        }

        GameData updated = new GameData(
                old.gameID(), old.whiteUsername(), old.blackUsername(),
                old.gameName(), chessGame
        );
        game.put(gameId, updated);
    }

}
