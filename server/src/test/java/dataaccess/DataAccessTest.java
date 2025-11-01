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

        assertNull(da.getUser(user.username()));
    }

    @Test
    void createUser() throws Exception {
        var user = new UserData("dawson", "grousehouse", "daws@gmail.com");
        DataAccess da = new MySqlDataAccess();
        da.createUser(user);
        assertNotNull(da.getUser(user.username()));
    }

    @Test
    void getUser() {
    }
}