package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import datamodel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserService;

import javax.xml.crypto.Data;
import java.util.Map;


public class Server {

    private final Javalin server;
    private UserService userService;
    private DataAccess dataAccess;


    public Server() {
        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register);
        server.post("/session", this::login);

        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx) throws Exception {
        try {
            var serializer = new Gson();
            String requestJson = ctx.body();
            var user = serializer.fromJson(requestJson, UserData.class);
            var authData = userService.register(user);
            var response = serializer.toJson(authData);
            ctx.status(200).result(response);
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if ("already taken".equals(msg)) {
                ctx.status(403).result("{ \"message\": \"Error: already taken\" }");
            } else if ("bad request".equals(msg)) {
                ctx.status(400).result("{ \"message\": \"Error: bad request\" }");
            } else {
                ctx.status(500).result("{ \"message\": \"Error: server down\" }");
            }
        }
    }

    private void login(Context ctx) throws Exception {
        try {
            var serializer = new Gson();
            String requestJson = ctx.body();
            var user = serializer.fromJson(requestJson, UserData.class);
            var authData = userService.login(user);
            var response = serializer.toJson(authData);
            ctx.status(200).result(response);
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if ("already taken".equals(msg)) {
                ctx.status(403).result("{ \"message\": \"Error: already taken\" }");
            } else if ("bad request".equals(msg)) {
                ctx.status(400).result("{ \"message\": \"Error: bad request\" }");
            } else if ("unauthorized".equals(msg)) {
                ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
            } else {
                ctx.status(500).result("{ \"message\": \"Error: server down\" }");
            }
        }
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
