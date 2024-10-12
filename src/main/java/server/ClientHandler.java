package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import lombok.Getter;
import common.message.ChatMessage;
import common.message.ConnectionMessage;
import common.message.ConnectionType;
import common.message.Message;

@Getter
public class ClientHandler implements Runnable {

    private final Socket socket;

    private final ObjectOutputStream out;

    private final ObjectInputStream in;

    private final ChatServer chatServer;


    public ClientHandler(Socket socket, ChatServer chatServer) {
        this.socket = socket;
        this.chatServer = chatServer;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void run() {
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

    private void handleIncomingMessage(Message message) throws IOException, ClassNotFoundException {
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

    private void handleConnectionMessage(ConnectionMessage c) {
        if (c.getType() == ConnectionType.JOIN) {
            chatServer.addClient(c.getUsername(), this, c);
        } else if (c.getType() == ConnectionType.EXIT) {
            chatServer.removeClient(c.getUsername(), this, c);
        }
    }
}