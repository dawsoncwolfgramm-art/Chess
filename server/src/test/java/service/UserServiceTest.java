package service;

import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.JoinGameRequest;
import datamodel.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @BeforeEach
    void clearAllData() throws Exception {
        var da = new MySqlDataAccess();
        da.clear();
    }

    @Test
    void clear() {
    }

    @Test
    void register() throws Exception {
        var user = new UserData("jow", "j", "j@j");
        var at = "xyz";

        var da = new MySqlDataAccess();
        var service = new UserService(da);
        AuthData res = service.register(user);
        assertNotNull(res);
        assertEquals(res.username(), user.username());
        assertNotNull(res.authToken());
        assertEquals(String.class, res.authToken().getClass());
    }

    @Test
    void registerTwice() throws Exception {
        var user = new UserData("jow", "j@j", "asdf@gmail.com");
        var at = "xyz";

        var da = new MySqlDataAccess();
        var service = new UserService(da);
        AuthData res = service.register(user);
        assertNotNull(res);
        assertThrows(Exception.class, () -> { // this arg is a callable function
            service.register(user);
        });
    }

    @Test
    void registerNoEmail() throws Exception {
        var user = new UserData("daws", "D@ws0n", "");
        var at = "xyz";

        var da = new MySqlDataAccess();
        var service = new UserService(da);
        assertThrows(Exception.class, () -> {
            service.register(user);
        });
    }

    @Test
    void loginNoPassword() throws Exception {
        var user = new UserData("daws", "D@ws0n", "daws@byu.edu");
        var user2 = new UserData("daws", "Dawson", "");
        var da = new MySqlDataAccess();
        var service = new UserService(da);
        AuthData registerRes = service.register(user);
        assertThrows(Exception.class, () -> {
            service.login(user2);
        });
    }

    @Test
    void loginNoRegister() throws Exception {
        var user = new UserData("daws", "D@ws0n", "daws@byu.edu");
        var da = new MySqlDataAccess();
        var service = new UserService(da);
        assertThrows(Exception.class, () -> {
            service.login(user);
        });
    }

    @Test
    void loginSuccess() throws Exception {
        var user = new UserData("daws", "D@ws0n", "daws@gmail.com");

        var da = new MySqlDataAccess();
        var service = new UserService(da);
        AuthData registerRes = service.register(user);
        AuthData res = service.login(user);
        assertEquals(res.username(), user.username());
        assertNotNull(res.authToken());
        assertEquals(String.class, res.authToken().getClass());
    }


    @Test
    void logoutSuccess() throws Exception {
        var user = new UserData("daws", "D@ws0n", "daws@gmail.com");
        var da = new MySqlDataAccess();
        var service = new UserService(da);
        AuthData registerRes = service.register(user);
        assertEquals(registerRes.username(), user.username());
        assertNotNull(registerRes.authToken());
        assertNotNull(da.getUser(registerRes.authToken()));
        service.logout(registerRes.authToken());
        Optional<AuthData> optAuthData = da.getAuth(registerRes.authToken());
        assertEquals(Optional.empty(), optAuthData);
    }

    @Test
    void logoutBadAuth() throws Exception {
        var user = new UserData("daws", "D@ws0n", "daws@gmail.com");
        var auth = new AuthData("daws", "abcdefghijklmnopqrstuvwxyz");
        var da = new MySqlDataAccess();
        var service = new UserService(da);
        AuthData registerRes = service.register(user);
        assertThrows(Exception.class, () -> {
            service.logout(auth.authToken());
        });
    }

    @Test
    void createGameSuccess() throws Exception {
        var user = new UserData("daws", "D@ws0n", "daws@byu.edu");
        var game = new GameData(0, null, null, "Lonely", null);
        var da = new MySqlDataAccess();
        var service = new UserService(da);
        var auth = service.register(user);
        assertEquals(1, service.createGame(auth.authToken(), game));
    }

    @Test
    void createGamenameNull() throws Exception {
        var user = new UserData("daws", "D@ws0n", "daws@byu.edu");
        var game = new GameData(0, null, null, null, null);
        var da = new MySqlDataAccess();
        var service = new UserService(da);
        var auth = service.register(user);
        assertThrows(Exception.class, () -> {
            service.createGame(auth.authToken(), game);
        });
    }


    @Test
    void joinGameSuccess() throws Exception {
        var da = new MySqlDataAccess();
        var service = new UserService(da);
        var user = new UserData("daws", "D@ws0n", "daws@gmail.com");
        AuthData registerRes = service.register(user);
        GameData game = new GameData(1, null, null, "lonly", null);
        service.createGame(registerRes.authToken(), game);
        JoinGameRequest joinGameReq = new JoinGameRequest("white", 1);
        service.joinGame(registerRes.authToken(), joinGameReq);
//        assertNotNull(da.getGame(game.gameID()).whiteUsername());
    }

    @Test
    void joinGameFail() throws Exception {
        var da = new MySqlDataAccess();
        var service = new UserService(da);
        var user = new UserData("daws", "D@ws0n", "daws@gmail.com");
        AuthData registerRes = service.register(user);
        GameData game = new GameData(1, null, null, "lonly", null);
        service.createGame(registerRes.authToken(), game);
        JoinGameRequest joinGameReq = new JoinGameRequest("green", 1);
        assertThrows(Exception.class, () -> {
            service.joinGame(registerRes.authToken(), joinGameReq);
        });
    }

    @Test
    void listGameSuccess() throws Exception {
        var da = new MySqlDataAccess();
        var service = new UserService(da);
        var user = new UserData("daws", "D@ws0n", "daws@gmail.com");
        AuthData registerRes = service.register(user);
        GameData game = new GameData(1, null, null, "lonly", null);
        service.createGame(registerRes.authToken(), game);
        JoinGameRequest joinGameReq = new JoinGameRequest("white", 1);
        service.joinGame(registerRes.authToken(), joinGameReq);
        GameData game2 = new GameData(2, null, null, "sturat", null);
        service.createGame(registerRes.authToken(), game2);
        assertEquals(2, service.listGames(registerRes.authToken()).size());
    }

    @Test
    void listGameFail() throws Exception {
        var da = new MySqlDataAccess();
        var service = new UserService(da);
        var user = new UserData("daws", "D@ws0n", "daws@gmail.com");
        AuthData registerRes = service.register(user);
        GameData game = new GameData(1, null, null, "lonly", null);
        service.createGame(registerRes.authToken(), game);
        JoinGameRequest joinGameReq = new JoinGameRequest("white", 1);
        service.joinGame(registerRes.authToken(), joinGameReq);
        GameData game2 = new GameData(2, null, null, "sturat", null);
        GameData game3 = new GameData(3, null, null, "dawsoin", null);
        service.createGame(registerRes.authToken(), game2);
        service.createGame(registerRes.authToken(), game3);
        service.logout(registerRes.authToken());
        assertThrows(Exception.class, () -> {
            service.listGames(registerRes.authToken());
        });
    }

}
