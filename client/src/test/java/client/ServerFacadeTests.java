package client;

import com.google.gson.Gson;
import datamodel.AuthData;
import datamodel.GameData;
import org.junit.jupiter.api.*;
import server.Server;

import java.net.http.HttpClient;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import datamodel.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static String serverUrl;


    @BeforeEach
    public void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverUrl = "http://localhost:" + port;
        serverFacade = new ServerFacade(serverUrl);
        try {
            serverFacade.clear();
        } catch (Exception e) {
            System.out.println("EXCEPTION CAUGHT AT THE SERVERFACADETESTS");
        }
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    void registerSuccess() throws Exception {
        String[] params = {"Dawson", "Wolfgramm", "D@Dwolf.com"};
        AuthData result = serverFacade.register(params);
        assertNotNull(result);
    }

    @Test
    void registerFail() throws Exception {
        String[] params = {"Dawson", "Wolfgramm", "D@Dwolf.com"};
        AuthData result = serverFacade.register(params);
        assertThrows(Exception.class, () -> serverFacade.register(params));
    }

    @Test
    void loginSuccess() throws Exception {
        String[] params = {"Dawson", "Wolfgramm", "D@Dwolf.com"};
        String[] loginInfo = {"Dawson", "Wolfgramm"};
        AuthData result = serverFacade.register(params);
        assertNotNull(result);
        AuthData loginAuth = serverFacade.login(loginInfo);
        assertNotNull(loginAuth);
    }

    @Test
    void loginFail() throws Exception {
        String[] params = {"Dawson", "Wolfgramm", "D@Dwolf.com"};
        String[] loginInfo = {"Dawson", "wolfgramm"};
        AuthData result = serverFacade.register(params);
        assertThrows(Exception.class, () -> serverFacade.login(loginInfo));
    }

    @Test
    void logoutSuccess() throws Exception {
        String[] params = {"Dawson", "Wolfgramm", "D@Dwolf.com"};
        String[] loginInfo = {"Dawson", "Wolfgramm"};
        AuthData result = serverFacade.register(params);
        assertNotNull(result);
        AuthData loginAuth = serverFacade.login(loginInfo);
        serverFacade.logout(loginAuth.authToken());
        assertThrows(Exception.class, () -> serverFacade.logout(loginAuth.authToken()));
    }

    @Test
    void logoutFail() throws Exception {
        String[] params = {"Dawson", "Wolfgramm", "D@Dwolf.com"};
        String[] loginInfo = {"Dawson", "Wolfgramm"};
        String randomAuth = "1234987as0dihvoij0qy3r098qywer";
        AuthData result = serverFacade.register(params);
        assertNotNull(result);
        AuthData loginAuth = serverFacade.login(loginInfo);
        assertThrows(Exception.class, () -> serverFacade.logout(randomAuth));
    }

    @Test
    void createGameSuccess() throws Exception {
        String[] params = {"Dawson", "Wolfgramm", "D@Dwolf.com"};
        String gameName = "SiblingBattle";
        AuthData result = serverFacade.register(params);
        Integer gameId = serverFacade.createGame(result.authToken(), gameName);
        assertInstanceOf(Integer.class, gameId);
    }

    @Test
    void createGameFail() throws Exception {
        String[] params = {"Dawson", "Wolfgramm", "D@Dwolf.com"};
        String gameName = null;
        AuthData result = serverFacade.register(params);
        assertThrows(Exception.class, () -> serverFacade.createGame(result.authToken(), gameName));
    }


    @Test
    void listGameSuccess() throws Exception {
        String[] params = {"Dawson", "Wolfgramm", "D@Dwolf.com"};
        String gameName = "SiblingBattle";
        AuthData result = serverFacade.register(params);
        Integer gameId = serverFacade.createGame(result.authToken(), gameName);
        List<GameData> games = serverFacade.listGames(result.authToken());
        assertNotNull(games);
    }

    @Test
    void listGameFail() throws Exception {
        String[] params = {"Dawson", "Wolfgramm", "D@Dwolf.com"};
        String gameName = "SiblingBattle";
        String randomAuth = "1p2384u09as8hd09813h45";
        AuthData result = serverFacade.register(params);
        Integer gameId = serverFacade.createGame(result.authToken(), gameName);
        assertThrows(Exception.class, () -> serverFacade.listGames(randomAuth));
    }

    @Test
    void joinGameSuccess() throws Exception {
        String[] params = {"Dawson", "Wolfgramm", "D@Dwolf.com"};
        String gameName = "SiblingBattle";
        AuthData result = serverFacade.register(params);
        Integer gameId = serverFacade.createGame(result.authToken(), gameName);
        serverFacade.joinGame(result.authToken(), "white", "1");
        List<GameData> games = serverFacade.listGames(result.authToken());
        GameData game = games.getFirst();
        Assertions.assertEquals("Dawson", game.whiteUsername());
    }

    @Test
    void joinGameFail() throws Exception {
        String[] params = {"Dawson", "Wolfgramm", "D@Dwolf.com"};
        String gameName = "SiblingBattle";
        AuthData result = serverFacade.register(params);
        Integer gameId = serverFacade.createGame(result.authToken(), gameName);
        assertThrows(Exception.class, () -> serverFacade.joinGame(result.authToken(), "blue", "1"));
    }

    @Test
    void clear() throws Exception {
        String[] params = {"Dawson", "Wolfgramm", "D@Dwolf.com"};
        String[] loginInfo = {"Dawson", "Wolfgramm"};
        String gameName = "SiblingBattle";
        AuthData result = serverFacade.register(params);
        Integer gameId = serverFacade.createGame(result.authToken(), gameName);
        serverFacade.joinGame(result.authToken(), "white", "1");
        List<GameData> games = serverFacade.listGames(result.authToken());
        assertNotNull(games);
        serverFacade.clear();
        assertThrows(Exception.class, () -> serverFacade.login(loginInfo));
    }
}
