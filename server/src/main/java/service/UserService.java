package service;

//import datamodel.RegistrationResult;

import datamodel.AuthData;
import dataaccess.DataAccess;
import datamodel.UserData;

public class UserService {
    private final DataAccess dataAccess;

    public void clear() {

    }

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws Exception {
//        if (dataAccess.getUser(user.username()) == null || dataAccess.getUser(user.email()) == null || dataAccess.getUser(user.password()) == null) {
//            throw new Exception("bad request");
//        }
        if (dataAccess.getUser(user.username()) != null) {
            throw new Exception("already exists");
        }
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
