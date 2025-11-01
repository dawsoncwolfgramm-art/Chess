package dataaccess;

import datamodel.*;

import java.util.Optional;

import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    @Test
    void clear() throws Exception {
        var user = new UserData("jow", "j@j", "j");
//        var auth = new AuthData("jow", "120983rfpajsg0981345");
//        var game = new GameData(1, "white", "black", "colors", null);
        DataAccess da = new MySqlDataAccess();
        da.createUser(user);
        da.clear();
        var optUserData = da.getUser(user.username());
        assertEquals(Optional.empty(), optUserData);
    }

    @Test
    void createUser() throws Exception {
        var user = new UserData("dawson", "grousehouse", "daws@gmail.com");
        DataAccess da = new MySqlDataAccess();
        da.createUser(user);
        var optUserData = da.getUser(user.username());
        assertTrue(optUserData.isPresent());
        UserData userData = optUserData.get();
        assertNotNull(da.getUser(userData.username()));
    }

    @Test
    void getUser() throws Exception {
        var user = new UserData("dawson", "grousehouse", "daws@gmail.com");
        DataAccess da = new MySqlDataAccess();
        da.createUser(user);
        var optUserData = da.getUser(user.username());
        UserData userData = optUserData.get();
        assertEquals(userData.username(), user.username());
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


}