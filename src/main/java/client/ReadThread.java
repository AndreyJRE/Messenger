package client;

import common.message.ChatMessage;
import common.message.ConnectionMessage;
import common.message.ConnectionResponseMessage;
import common.message.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

class ReadThread extends Thread {

    private final ObjectInputStream reader;

    private final ChatClient client;

    public ReadThread(Socket socket, ChatClient client) {
        this.client = client;
        try {
            reader = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {

        while (true) {
            try {
                // Receive the Message object from the server
                Message message = (Message) reader.readObject();
                switch (message) {
                    case ChatMessage c -> System.out.println(c);
                    case ConnectionMessage c -> System.out.println(c);
                    case ConnectionResponseMessage c -> {
                        switch (c.getResponseType()) {
                            case ESTABLISHED -> {
                                System.out.println("You are connected to the chat");
                                client.setConnectedToChat(true);
                            }
                            case REJECTED ->
                                System.out.println("Username is already taken. Please try another one.");
                        }
                    }
                    default -> System.out.println("Unknown message type: " + message);
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Connection to the server is lost");
                break;
            }
        }
    }
}
