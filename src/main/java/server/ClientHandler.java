package server;

import common.message.ChatMessage;
import common.message.ConnectionMessage;
import common.message.ConnectionType;
import common.message.Message;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import lombok.Getter;

@Getter
public class ClientHandler implements Runnable {

    private final Socket socket;

    private final ChatServer chatServer;

    private ObjectInputStream in;

    private ObjectOutputStream out;

    private String username;


    public ClientHandler(Socket socket, ChatServer chatServer) {
        this.socket = socket;
        this.chatServer = chatServer;

    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            while (true) {
                if (socket.isClosed()) {
                    handleClientDisconnection();
                    break;
                }
                try {
                    Message message = (Message) in.readObject();
                    handleIncomingMessage(message);
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error reading from client: " + e.getMessage());
        } finally {
            try {
                socket.close();
                handleClientDisconnection();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
                handleClientDisconnection();
            }
        }
    }

    private void handleClientDisconnection() {
        if (username != null) {
            chatServer.removeClient(username, this, new ConnectionMessage(username, ConnectionType.EXIT));
        }
    }

    public void handleIncomingMessage(Message message) throws IOException, ClassNotFoundException {
        switch (message) {
            case ChatMessage c -> this.chatServer.broadcastMessage(c, this);
            case ConnectionMessage c -> handleConnectionMessage(c);
            default -> {
                System.out.println("Unknown message type: " + message);
                System.out.println("Closing connection to client");
                this.socket.close();
            }
        }
    }

    public void handleConnectionMessage(ConnectionMessage c) {
        String s = c.getUsername();
        switch (c.getType()) {
            case JOIN -> {
                chatServer.addClient(s, this, c);
                username = s;
            }
            case EXIT -> chatServer.removeClient(s, this, c);
            default -> throw new IllegalArgumentException("Unknown connection type: " + c.getType());
        }
    }
}