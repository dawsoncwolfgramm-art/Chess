package dataaccess;


import datamodel.GameData;
import datamodel.UserData;
import datamodel.AuthData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {

    private HashMap<String, UserData> users = new HashMap<>();
    private HashMap<String, AuthData> auth = new HashMap<>();
    private HashMap<String, GameData> data = new HashMap<>();

    public void clear() {
        users.clear();
        auth.clear();
    }

    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void addAuth(AuthData authData) {
        auth.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auth.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        auth.remove(authToken);
    }

    public void addGame(GameData gameData) {
        data.put(gameData.gameName(), gameData);
    }


}
