package client;

import common.message.ChatMessage;
import common.message.ConnectionMessage;
import common.message.ConnectionResponseMessage;
import common.message.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ReadThread extends Thread {

    private final Socket socket;
    private final ChatClient client;
    private ObjectInputStream reader;

    public void run() {
        try {
            reader = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error getting input stream: " + e.getMessage());
        }
        while (true) {
            try {
                Message message = (Message) reader.readObject();
                handleMessage(message);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Connection to the server is lost");
                client.disconnect();
                break;
            }
        }
    }

    public void handleMessage(Message message) {
        switch (message) {
            case ChatMessage c -> System.out.println(c);
            case ConnectionMessage c -> System.out.println(c);
            case ConnectionResponseMessage c -> {
                switch (c.getResponseType()) {
                    case ESTABLISHED -> {
                        System.out.println("You are connected to the chat");
                        client.setConnectedToChat(true);
                    }
                    case REJECTED -> System.out.println("Username is already taken. Please try another one.");
                }
            }
            default -> System.out.println("Unknown message type: " + message);
        }
    }
}
