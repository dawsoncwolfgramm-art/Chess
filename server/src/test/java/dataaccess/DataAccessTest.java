package dataaccess;

import datamodel.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    @Test
    void clear() {
        var user = new UserData("jow", "j@j", "j");
        DataAccess da = new MemoryDataAccess();
        da.createUser(user);
        da.clear();
        assertNull(da.getUser(user.username()));

    }

    @Test
    void createUser() {
    }

    @Test
    void getUser() {
    }
}