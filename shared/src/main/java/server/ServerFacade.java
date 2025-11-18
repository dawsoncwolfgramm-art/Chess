package server;

import java.net.http.HttpClient;


public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public String register(String [] params) {

    }

    public void login() {

    }

    public void logout() {

    }

    public

}
