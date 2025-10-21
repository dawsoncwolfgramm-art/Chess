package service;

import datamodel.AuthData;
import dataaccess.DataAccess;
import datamodel.UserData;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() {
        dataAccess.clear();
    }


    public AuthData register(UserData user) throws Exception {
        if (user == null || user.username() == null || user.username().isBlank() ||
                user.email() == null || user.email().isBlank() ||
                user.password() == null || user.password().isBlank()) {
            throw new Exception("bad request");
        }
        if (dataAccess.getUser(user.username()) != null) {
            throw new Exception("already taken");
        }
        dataAccess.createUser(user);
        AuthData authData = new AuthData(user.username(), generateAuthToken());
        dataAccess.addAuth(authData);
        return authData;
    }

    public AuthData login(UserData user) throws Exception {
        if (user == null || user.username() == null || user.username().isBlank() ||
                user.password() == null || user.password().isBlank()) {
            throw new Exception("bad request");
        }
        if (dataAccess.getUser(user.username()) == null) {
            throw new Exception("unauthorized");
        }
        UserData userData = dataAccess.getUser(user.username());
        if (!user.password().equals(userData.password())) {
            throw new Exception("unauthorized");
        }
        AuthData authData = new AuthData(user.username(), generateAuthToken());
        dataAccess.addAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws Exception {
        if (authToken == null || authToken.isBlank()) {
            throw new Exception("unauthorized");
        }

        if (dataAccess.getAuth(authToken) == null) {
            throw new Exception("unauthorized");
        }
        dataAccess.deleteAuth(authToken);
    }

    //use the script they gave you to generate the authToken
    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    private void createGame(String authToken)
}
