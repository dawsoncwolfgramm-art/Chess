package dataaccess;


import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MySqlDataAccess implements DataAccess {
    public MySqlDataAccess() throws Exception {
        example();
//        configureDatabase();
    }


    public void clear() {

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

    private void configureDatabase() throws Exception {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
//            for (String statement : createStatements) {
//                try (var preparedStatement = conn.prepareStatement(statement)) {
//                    preparedStatement.executeUpdate();
//                }
//            }
//        } catch (SQLException ex) {
//            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    public void example() throws Exception {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT 1+1")) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                System.out.println(rs.getInt(1));
            }
        }
    }
}

