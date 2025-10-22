package service;

import dataaccess.MemoryDataAccess;
import datamodel.AuthData;
import datamodel.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    void failedRegister() throws Exception {
        var user = new UserData("jow", "j@j", "j");
        var at = "xyz";

        var da = new MemoryDataAccess();
        var service = new UserService(da);
        AuthData res = service.register(user);
        assertNotNull(res);
//        assertThrows
        assertEquals(res.username(), user.username());
        assertNotNull(res.authToken());
        assertEquals(String.class, res.authToken().getClass());
    }

    @Test
    void login() throws Exception {
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
}
