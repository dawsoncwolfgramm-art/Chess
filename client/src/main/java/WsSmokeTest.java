import client.WebsocketCommunicator;

public class WsSmokeTest {
    public static void main(String[] args) throws Exception {
        WebsocketCommunicator ws =
                new WebsocketCommunicator("http://localhost:8080",
                        message -> System.out.println("RECEIVED: " + message.getServerMessageType()));

        System.out.println("WebSocket created.");
        Thread.sleep(100000); // keep program alive
    }
}
