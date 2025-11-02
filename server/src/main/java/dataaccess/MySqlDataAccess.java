package dataaccess;


import chess.ChessGame;
import com.google.gson.Gson;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.*;

public class MySqlDataAccess implements DataAccess {
    private final Gson gson = new Gson();


    public MySqlDataAccess() throws Exception {
        configureDatabase();
    }

    private final String[] clearStatements = {
            "TRUNCATE TABLE gamedata;",
            "TRUNCATE TABLE authdata;",
            "TRUNCATE TABLE userdata;",
            "TRUNCATE TABLE joingamerequest;",
            "ALTER TABLE gamedata AUTO_INCREMENT = 1;"
    };

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : clearStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("dataaccess problem");
        }

    }

    @Override
    public void createUser(UserData user) throws Exception {
        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement("INSERT INTO userdata(username, password, email) VALUES(?, ?, ?)");) {
            statement.setString(1, user.username());
            statement.setString(2, user.password());
            statement.setString(3, user.email());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("dataaccess problem");
        }
    }

    @Override
    public Optional<UserData> getUser(String username) throws Exception {
        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement("SELECT * FROM userdata WHERE username = ?");) {
            statement.setString(1, username);
            var seq = statement.executeQuery();
            if (seq.next()) {
                return Optional.of(new UserData(seq.getString("username"),
                        seq.getString("password"),
                        seq.getString("email")));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("dataaccess problem");
        }
    }

    @Override
    public Optional<AuthData> getAuth(String auth) throws Exception {
        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement("SELECT * FROM authdata WHERE authToken = ?");) {
            statement.setString(1, auth);
            var seq = statement.executeQuery();
            if (seq.next()) {
                return Optional.of(new AuthData(seq.getString("username"),
                        seq.getString("authToken")));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("dataaccess problem");
        }
    }

    @Override
    public void addAuth(AuthData auth) throws Exception {
        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement("INSERT INTO authdata(username, authToken) VALUES(?, ?)");) {
            statement.setString(1, auth.username());
            statement.setString(2, auth.authToken());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("dataaccess problem");
        }
    }

    @Override
    public void deleteAuth(String auth) throws Exception {
        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement("DELETE FROM authdata WHERE authToken = ?");) {
            statement.setString(1, auth);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("dataaccess problem");
        }
    }

    @Override
    public void addGame(GameData gameData) throws Exception {
        ChessGame game = new ChessGame();
        String gameJson = new Gson().toJson(game);
        String sql = "INSERT INTO gamedata(whiteUsername, blackUsername, gameName, chessGame) VALUES(?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(sql);) {
            statement.setString(1, gameData.whiteUsername());
            statement.setString(2, gameData.blackUsername());
            statement.setString(3, gameData.gameName());
            statement.setString(4, gameJson);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("dataaccess problem");
        }
    }

    @Override
    public List<GameData> getAllGames() throws Exception {
        return List.of();
    }

    @Override
    public Optional<GameData> getGame(int gameId) throws Exception {
        String sql = "SELECT * FROM gamedata WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(sql);) {
            statement.setInt(1, gameId);
            var seq = statement.executeQuery();
            if (seq.next()) {
                return Optional.of(new GameData(seq.getInt("gameID"),
                        seq.getString("whiteUsername"),
                        seq.getString("blackUsername"),
                        seq.getString("gameName"),
                        seq.getString("chessGame")));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("dataaccess problem");
        }
    }

    @Override
    public AuthData getPlayerName(String auth) throws Exception {
        return null;
    }

    @Override
    public void updateGame(int gameId, String whiteUsername, String blackUsername, String gameName) throws Exception {

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
              `gameID` int PRIMARY KEY NOT NULL,
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

