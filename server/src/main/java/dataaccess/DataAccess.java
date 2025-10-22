package dataaccess;

import chess.ChessGame;
import datamodel.*;

import java.util.Collection;

public interface DataAccess {
    void clear();

    void createUser(UserData user);

    UserData getUser(String username);

    AuthData getAuth(String auth);

    void addAuth(AuthData authData);

    void deleteAuth(String auth);

    void addGame(GameData gameData);

    Collection<GameData> getAllGames();

    GameData getGame(int gameId);

    AuthData getPlayerName(String auth);

    String isColorNull(Integer gameId, String color);

    void updateGame(int gameId, String whiteUsername,
                    String blackUsername, String gameName, ChessGame game);
}
