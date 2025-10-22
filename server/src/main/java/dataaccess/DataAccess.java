package dataaccess;

import datamodel.*;

import java.util.Collection;
import java.util.List;

public interface DataAccess {
    void clear();

    void createUser(UserData user);

    UserData getUser(String username);

    AuthData getAuth(String auth);

    void addAuth(AuthData authData);

    void deleteAuth(String auth);

    void addGame(GameData gameData);

    List<GameData> getAllGames();

    GameData getGame(int gameId);

    AuthData getPlayerName(String auth);

    void updateGame(int gameId, String whiteUsername,
                    String blackUsername, String gameName);
}
