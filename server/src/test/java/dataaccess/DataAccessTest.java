package dataaccess;

import datamodel.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import service.BadRequestException;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    @Test
    void clear() throws Exception {
        var user = new UserData("jow", "j@j", "j");
        var auth = new AuthData("jow", "120983rfpajsg0981345");
        var game = new GameData(1, "white", "black", "colors", null);
        DataAccess da = new MySqlDataAccess();
        da.createUser(user);
        da.addAuth(auth);
        da.addGame(game);
        da.clear();
        var optUserData = da.getUser(user.username());
        assertEquals(Optional.empty(), optUserData);
    }

    @Test
    void createUser() throws Exception {
        var user = new UserData("dawson", "asdfghjkl;", "daws@gmail.com");
        DataAccess da = new MySqlDataAccess();
        da.createUser(user);
        var optUserData = da.getUser(user.username());
        assertTrue(optUserData.isPresent());
        UserData userData = optUserData.get();
        assertNotNull(da.getUser(userData.username()));
    }

    @Test
    void createUserFailDuplicateUsername() throws Exception {
        var u1 = new UserData("walker", "asdfghjkl", "a@a.com");
        var u2 = new UserData("wyatt", "asdfghjkl", "b@b.com"); // same username (PK)
        DataAccess da = new MySqlDataAccess();
        da.clear();

        da.createUser(u1);
        assertThrows(DataAccessException.class, () -> da.createUser(u2));
    }

    @Test
    void createUserFailNulls() throws Exception {
        var bad = new UserData("dawson", null, null);
        DataAccess da = new MySqlDataAccess();
        da.clear();

        assertThrows(DataAccessException.class, () -> da.createUser(bad));
    }

    @Test
    void getUser() throws Exception {
        var user = new UserData("dawson", "asdfghjkl", "daws@gmail.com");
        DataAccess da = new MySqlDataAccess();
        da.createUser(user);
        var optUserData = da.getUser(user.username());
        UserData userData = optUserData.get();
        assertEquals(userData.username(), user.username());
    }

    @Test
    void getUserFail() throws Exception {
        DataAccess da = new MySqlDataAccess();
        da.clear();
        var opt = da.getUser("dawson");
        assertTrue(opt.isEmpty());
    }

    @Test
    void addAuth() throws Exception {
        AuthData auth = new AuthData("dawson", "12345678");
        DataAccess da = new MySqlDataAccess();
        da.clear();
        da.addAuth(auth);
        var optAuthData = da.getAuth(auth.authToken());
        assertTrue(optAuthData.isPresent());
        AuthData authData = optAuthData.get();
        assertNotNull(da.getUser(authData.authToken()));
    }

    @Test
    void addAuthFailDuplicateToken() throws Exception {
        var user = new UserData("dawson", "p12340987123", "a@a.com");
        var a1 = new AuthData("walker", "12341234");
        var a2 = new AuthData("wyatt", "12341234");
        DataAccess da = new MySqlDataAccess();
        da.clear();
        da.createUser(user);
        da.addAuth(a1);
        assertThrows(DataAccessException.class, () -> da.addAuth(a2));
    }

    @Test
    void getAuthFailNotFound() throws Exception {
        DataAccess da = new MySqlDataAccess();
        da.clear();
        var opt = da.getAuth("missing");
        assertTrue(opt.isEmpty());
    }


    @Test
    void deleteAuth() throws Exception {
        AuthData auth = new AuthData("dawson", "12345678");
        AuthData authTwo = new AuthData("lauren", "87654321");
        DataAccess da = new MySqlDataAccess();
        da.clear();
        da.addAuth(auth);
        da.addAuth(authTwo);
        da.deleteAuth(auth.authToken());
        var optAuthData = da.getAuth(auth.authToken());
        assertEquals(Optional.empty(), optAuthData);
        var optAuthDataTwo = da.getAuth((authTwo.authToken()));
        assertTrue(optAuthDataTwo.isPresent());
    }

    @Test
    void deleteAuthFail() throws Exception {
        DataAccess da = new MySqlDataAccess();
        da.clear();
        da.deleteAuth("doesn't-exist");
        assertTrue(da.getAuth("doesn't-exist").isEmpty());
    }

    @Test
    void addGame() throws Exception {
        GameData game = new GameData(0, null, null, "siblingFight", null);
        DataAccess da = new MySqlDataAccess();
        da.clear();
        da.addGame(game);

    }

    @Test
    void getGame() throws Exception {
        GameData game = new GameData(0, null, null, "siblingFight", null);
        DataAccess da = new MySqlDataAccess();
        da.clear();
        da.addGame(game);
        da.getGame(1);
    }

    @Test
    void addGameFail() throws Exception {
        var badGame = new GameData(0, null, null, null, null);
        DataAccess da = new MySqlDataAccess();
        da.clear();
        assertThrows(DataAccessException.class, () -> da.addGame(badGame));
    }

    @Test
    void getGameFail() throws Exception {
        DataAccess da = new MySqlDataAccess();
        da.clear();
        var opt = da.getGame(1234);
        assertTrue(opt.isEmpty());
    }

    @Test
    void getListGames() throws Exception {
        GameData game1 = new GameData(0, null, null, "siblingFight", null);
        GameData game2 = new GameData(1, null, null, "parentsFight", null);
        GameData game3 = new GameData(2, null, null, "inLawFights", null);
        DataAccess da = new MySqlDataAccess();
        da.clear();
        da.addGame(game1);
        da.addGame(game2);
        da.addGame(game3);
        List<GameData> games = da.getAllGames();
        assertEquals(3, games.size());
    }

    @Test
    void updateGame() throws Exception {
        GameData game1 = new GameData(0, null, null, "siblingFight", null);
        String whiteUser = "dawson";
        String blackUser = "sturat";
        String gameName = "siblingFight";
        DataAccess da = new MySqlDataAccess();
        da.clear();
        da.addGame(game1);
        da.updateGame(1, whiteUser, blackUser, gameName);
    }

}