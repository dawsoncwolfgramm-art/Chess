package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import datamodel.GameData;
import datamodel.UserData;
import datamodel.JoinGameRequest;
import io.javalin.*;
import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UnauthorizedException;
import service.UserService;

import java.util.List;
import java.util.Map;


public class Server {

    private final Javalin server;
    private UserService userService;
    private DataAccess dataAccess;


    public Server() {
        try {
            dataAccess = new MySqlDataAccess();
        } catch (Exception ex) {
            dataAccess = new MemoryDataAccess();
        }
        userService = new UserService(dataAccess);
        server = Javalin.create(config -> config.staticFiles.add("web"));
        server.delete("db", this::clear);
        server.post("user", this::register);
        server.post("/session", this::login);
        server.delete("/session", this::logout);
        server.post("/game", this::createGame);
        server.get("/game", this::listGames);
        server.put("/game", this::joinGame);

        // Register your endpoints and exception handlers here.

    }

    private void clear(Context ctx) {
        try {
            userService.clear();
            ctx.status(200).result("{}");
        } catch (Exception ex) {
            ctx.status(500).result(new Gson().toJson(Map.of("message", ex.getMessage())));
        }

    }

    private void register(Context ctx) {
        try {
            var serializer = new Gson();
            String requestJson = ctx.body();
            var user = serializer.fromJson(requestJson, UserData.class);
            var authData = userService.register(user);
            var response = serializer.toJson(authData);
            ctx.status(200).result(response);
        } catch (BadRequestException ex) {
            ctx.status(400).result("{ \"message\": \"Error: bad request\" }");
        } catch (AlreadyTakenException ex) {
            ctx.status(403).result("{ \"message\": \"Error: already taken\" }");
        } catch (Exception ex) {
            respondError(ctx, ex);
        }
    }

    private void login(Context ctx) {
        try {
            var serializer = new Gson();
            String requestJson = ctx.body();
            var user = serializer.fromJson(requestJson, UserData.class);
            var authData = userService.login(user);
            var response = serializer.toJson(authData);
            ctx.status(200).result(response);
        } catch (BadRequestException ex) {
            ctx.status(400).result("{ \"message\": \"Error: bad request\" }");
        } catch (UnauthorizedException ex) {
            ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
        } catch (Exception ex) {
            respondError(ctx, ex);
        }
    }

    private void logout(Context ctx) {
        try {
            String data = ctx.header("authorization");
            userService.logout(data);
            ctx.status(200).result("{}");
        } catch (UnauthorizedException ex) {
            ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
        } catch (Exception ex) {
            respondError(ctx, ex);
        }
    }

    private void createGame(Context ctx) {
        try {
            String data = ctx.header("authorization");
            var serializer = new Gson();
            String requestJson = ctx.body();
            var game = serializer.fromJson(requestJson, GameData.class);
            int gameId = userService.createGame(data, game);
            ctx.status(200).result("{ \"gameID\": " + gameId + " }");
        } catch (BadRequestException ex) {
            ctx.status(400).result("{ \"message\": \"Error: bad request\" }");
        } catch (UnauthorizedException ex) {
            ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
        } catch (Exception ex) {
            respondError(ctx, ex);
        }
    }

    private void listGames(Context ctx) {
        try {
            String token = ctx.header("authorization");
            var serializer = new Gson();
            List<GameData> gameList = userService.listGames(token);
            var returnString = String.format("{ \"games\": %s }", serializer.toJson(gameList));
            ctx.status(200).result(returnString);
        } catch (BadRequestException ex) {
            ctx.status(400).result("{ \"message\": \"Error: bad request\" }");
        } catch (UnauthorizedException ex) {
            ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
        } catch (Exception ex) {
            respondError(ctx, ex);
        }
    }

    private void joinGame(Context ctx) {
        try {
            String token = ctx.header("authorization");
            var serializer = new Gson();
            JoinGameRequest joinGameReq = serializer.fromJson(ctx.body(), JoinGameRequest.class);
            userService.joinGame(token, joinGameReq);
            ctx.status(200).result("{}");
        } catch (BadRequestException ex) {
            ctx.status(400).result("{ \"message\": \"Error: bad request\" }");
        } catch (UnauthorizedException ex) {
            ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
        } catch (AlreadyTakenException ex) {
            ctx.status(403).result("{ \"message\": \"Error: already taken\" }");
        } catch (Exception ex) {
            respondError(ctx, ex);
        }
    }

    private void respondError(Context ctx, Exception ex) {
        String msg = ex.getMessage();
        ctx.status(500).result("{ \"message\": \"Error: server down\" }");
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
