package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import datamodel.*;
import dataaccess.DataAccess;
import org.mindrot.jbcrypt.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;
    private final List<Integer> gamesIds = new ArrayList<>();

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() throws Exception {
        dataAccess.clear();
    }

    public AuthData register(UserData user) throws Exception {
        if (user.username() == null || user.username().isBlank() ||
                user.email() == null || user.email().isBlank() ||
                user.password() == null || user.password().isBlank()) {
            throw new BadRequestException("bad request");
        }
        if (dataAccess.getUser(user.username()).isPresent()) {
            throw new AlreadyTakenException("already taken");
        }
        var hashPwd = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        var storeUser = new UserData(user.username(), hashPwd, user.email());
        dataAccess.createUser(storeUser);
        var authData = new AuthData(user.username(), generateAuthToken());
        dataAccess.addAuth(authData);
        return authData;
    }

    public AuthData login(UserData user) throws Exception {
        if (user.username() == null || user.username().isBlank() ||
                user.password() == null || user.password().isBlank()) {
            throw new BadRequestException("bad request");
        }
        if (dataAccess.getUser(user.username()).isEmpty()) {
            throw new UnauthorizedException("unauthorized");
        }
        var optUserData = dataAccess.getUser(user.username());
        UserData userData = optUserData.get();
        if (!BCrypt.checkpw(user.password(), userData.password())) {
            throw new UnauthorizedException("unauthorized");
        }
        AuthData authData = new AuthData(user.username(), generateAuthToken());
        dataAccess.addAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws Exception {
        if (authToken == null || authToken.isBlank()) {
            throw new UnauthorizedException("unauthorized");
        }

        if (dataAccess.getAuth(authToken).isEmpty()) {
            throw new UnauthorizedException("unauthorized");
        }
        dataAccess.deleteAuth(authToken);
    }

    //use the script they gave you to generate the authToken
    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    public Integer createGame(String authToken, GameData userGameData) throws Exception {
        if (authToken == null || authToken.isBlank()) {
            throw new BadRequestException("bad request");
        }
        if (dataAccess.getAuth(authToken).isEmpty()) {
            throw new UnauthorizedException("unauthorized");
        }
        if (userGameData.gameName() == null) {
            throw new BadRequestException("bad request");
        }
        GameData gameDataMemory;
        int gameId;
        int num = 0;
        while (true) {
            if (!gamesIds.contains(num)) {
                gamesIds.add(num);
                gameDataMemory = new GameData(num, null,
                        null, userGameData.gameName(), null);
                gameId = dataAccess.addGame(gameDataMemory);
                break;
            }
            num++;
        }
        if (num != 1) {
            return gameId;
        }
        return gameDataMemory.gameID();
    }

    public List<GameData> listGames(String authToken) throws Exception {
        if (authToken == null || authToken.isBlank()) {
            throw new BadRequestException("bad request");
        }
        if (dataAccess.getAuth(authToken).isEmpty()) {
            throw new UnauthorizedException("unauthorized");
        }

        return new ArrayList<>(dataAccess.getAllGames());
    }

    public void joinGame(String authToken, JoinGameRequest joinData) throws Exception {
        if (authToken == null || authToken.isBlank()) {
            throw new BadRequestException("bad request");
        }
        if (dataAccess.getAuth(authToken).isEmpty()) {
            throw new UnauthorizedException("unauthorized");
        }
        if (joinData == null || joinData.playerColor == null || joinData.gameID == null) {
            throw new BadRequestException("bad request");
        }

        int gameId = joinData.gameID;
        Optional<AuthData> optPlayer = dataAccess.getAuth(authToken);
        AuthData player = optPlayer.get();
        Optional<GameData> optGameData = dataAccess.getGame(gameId);
        GameData game = optGameData.get();
        if (joinData.playerColor().equalsIgnoreCase("white")) {
            if (game.whiteUsername() != null) {
                throw new AlreadyTakenException("already taken");
            }
            dataAccess.updateGame(joinData.gameID, player.username(), game.blackUsername(), game.gameName());
        } else if (joinData.playerColor().equalsIgnoreCase("black")) {
            if (game.blackUsername() != null) {
                throw new AlreadyTakenException("already taken");
            }
            dataAccess.updateGame(joinData.gameID, game.whiteUsername(), player.username(), game.gameName());

        } else {
            throw new BadRequestException("bad request");
        }
    }
}
