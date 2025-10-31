package dataaccess;


import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class MySqlDataAccess implements DataAccess {
    private HashMap<String, UserData> users = new HashMap<>();
    private HashMap<String, AuthData> auth = new HashMap<>();
    private HashMap<Integer, GameData> game = new HashMap<>();


    public MySqlDataAccess() throws Exception {
        configureDatabase();
    }

    public void clear() {
        var statement = "TRUNICATE chess";
    }

    public void createUser(UserData user) {

    }

    public UserData getUser(String username) {
        return null;
    }

    public AuthData getAuth(String auth) {
        return null;
    }

    public void addAuth(AuthData authData) {

    }

    public void deleteAuth(String auth) {

    }

    public void addGame(GameData gameData) {

    }

    public List<GameData> getAllGames() {
        return List.of();
    }

    public GameData getGame(int gameId) {
        return null;
    }

    public AuthData getPlayerName(String auth) {
        return null;
    }

    public void updateGame(int gameId, String whiteUsername, String blackUsername, String gameName) {

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  gameData (
              `gameID` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) NULL,
              `blackUsername` varchar(256) NULL,
              `gameName` varchar(256) NOT NULL,
              `chessGame` TEXT NULL,
              INDEX(`gameID`),
              INDEX(`gameName`)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS  userData (
              `username` varchar(256) PRIMARY KEY NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              INDEX(`username`)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS  authData (
              `username` varchar(256) NOT NULL,
              `authToken` varchar(256) PRIMARY KEY NOT NULL,
              INDEX(`authToken`)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS joinGameRequest (
              `playerColor` varchar(256) NOT NULL,
              `gameID` int PRIMARY INT NOT NULL,
              INDEX(`gameID`)
            );
            """
    };

    private void configureDatabase() throws Exception {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new Exception(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}

