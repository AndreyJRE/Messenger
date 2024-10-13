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


    public ClientHandler(Socket socket, ChatServer chatServer) {
        this.socket = socket;
        this.chatServer = chatServer;

    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error getting input stream: " + e.getMessage());
        }
        try {
            while (true) {
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
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
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
        if (c.getType() == ConnectionType.JOIN) {
            chatServer.addClient(c.getUsername(), this, c);
        } else if (c.getType() == ConnectionType.EXIT) {
            chatServer.removeClient(c.getUsername(), this, c);
        }
    }
}