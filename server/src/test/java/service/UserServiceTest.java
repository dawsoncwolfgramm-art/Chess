package service;

import dataaccess.MemoryDataAccess;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Test
    void clear() {
    }

    @Test
    void register() throws Exception {
        var user = new UserData("jow", "j@j", "j");
        var at = "xyz";

        var da = new MemoryDataAccess();
        var service = new UserService(da);
        AuthData res = service.register(user);
        assertNotNull(res);
        assertEquals(res.username(), user.username());
        assertNotNull(res.authToken());
        assertEquals(String.class, res.authToken().getClass());
    }

    @Test
    void RegisterTwice() throws Exception {
        var user = new UserData("jow", "j@j", "asdf@gmail.com");
        var at = "xyz";

        var da = new MemoryDataAccess();
        var service = new UserService(da);
        AuthData res = service.register(user);
        assertNotNull(res);
        assertThrows(Exception.class, () -> { // this arg is a callable function
            service.register(user);
        });
    }

    @Test
    void RegisterNoEmail() throws Exception {
        var user = new UserData("daws", "D@ws0n", "");

        var at = "xyz";

        var da = new MemoryDataAccess();
        var service = new UserService(da);
        assertThrows(Exception.class, () -> {
            service.register(user);
        });
    }

    @Test
    void LoginNoPassword() throws Exception {
        var user = new UserData("daws", "D@ws0n", "daws@byu.edu");
        var user2 = new UserData("daws", "Dawson", "");
        var da = new MemoryDataAccess();
        var service = new UserService(da);
        AuthData registerRes = service.register(user);
        assertThrows(Exception.class, () -> {
            service.login(user2);
        });
    }

    @Test
    void LoginNoRegister() throws Exception {
        var user = new UserData("daws", "D@ws0n", "daws@byu.edu");
        var da = new MemoryDataAccess();
        var service = new UserService(da);
        assertThrows(Exception.class, () -> {
            service.login(user);
        });
    }

    @Test
    void loginSuccess() throws Exception {
        var user = new UserData("daws", "D@ws0n", "daws@gmail.com");

        var da = new MemoryDataAccess();
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
        var da = new MemoryDataAccess();
        var service = new UserService(da);
        AuthData registerRes = service.register(user);
        AuthData res = service.login(user);
        assertEquals(res.username(), user.username());
        assertNotNull(res.authToken());
        assertNull(da.getUser(res.authToken()));
        service.logout(res.authToken());
        assertNull(da.getAuth(res.authToken()));
    }

    @Test
    void logoutBadAuth() throws Exception {
        var user = new UserData("daws", "D@ws0n", "daws@gmail.com");
        var auth = new AuthData("daws", "abcdefghijklmnopqrstuvwxyz");
        var da = new MemoryDataAccess();
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
        var da = new MemoryDataAccess();
        var service = new UserService(da);
        var auth = service.register(user);
        assertEquals(1, service.createGame(auth.authToken(), game));
    }
}
