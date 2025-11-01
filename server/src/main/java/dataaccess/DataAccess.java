package dataaccess;

import datamodel.*;

import java.util.List;
import java.util.Optional;

public interface DataAccess {
    void clear() throws Exception;

    void createUser(UserData user) throws Exception;

    Optional<UserData> getUser(String username) throws Exception;

    Optional<AuthData> getAuth(String auth) throws Exception;

    void addAuth(AuthData authData) throws Exception;

    void deleteAuth(String auth) throws Exception;

    void addGame(GameData gameData) throws Exception;

    List<GameData> getAllGames() throws Exception;

    GameData getGame(int gameId) throws Exception;

    AuthData getPlayerName(String auth) throws Exception;

    void updateGame(int gameId, String whiteUsername,
                    String blackUsername, String gameName) throws Exception;
}
