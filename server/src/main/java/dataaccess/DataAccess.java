package dataaccess;

import datamodel.*;

public interface DataAccess {
    void clear();

    void createUser(UserData user);

    UserData getUser(String username);

    AuthData getAuth(String auth);

    void addAuth(AuthData authData);

    void deleteAuth(String auth);
}
