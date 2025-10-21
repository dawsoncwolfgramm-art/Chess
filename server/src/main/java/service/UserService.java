package service;

import datamodel.AuthData;
import dataaccess.DataAccess;
import datamodel.UserData;

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
        return authData;
    }

    public AuthData login(UserData user) throws Exception {
        if (user == null || user.username() == null || user.username().isBlank() ||
                user.email() == null || user.email().isBlank() ||
                user.password() == null || user.password().isBlank()) {
            throw new Exception("bad request");
        }

        if ()
        if (dataAccess.getUser(user.username()) != null) {
            throw new Exception("already taken");
        }

//        dataAccess.getUser(user.username()) == user.username()
        dataAccess.createUser(user);
        AuthData authData = new AuthData(user.username(), generateAuthToken());
        return authData;
    }

    //use the script they gave you to generate the authToken
    private String generateAuthToken() {
        return "xyz";
    }


/*    public void RegistrationResult(User user) {
        priavte DataAccess
        new RegistrationResult(user.username(), "zyyz");

    }*/
}
