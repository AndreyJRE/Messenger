package server;

import common.message.ConnectionMessage;
import common.message.ConnectionResponseMessage;
import common.message.ConnectionResponseType;
import common.message.Message;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatServer implements Runnable {

    private final int port;
    private final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private volatile boolean isRunning = false;

    public ChatServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        this.isRunning = true;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                new Thread(clientHandler).start();
            }
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error in the server: " + e.getMessage());
            this.isRunning = false;
        }
    }

    public void broadcastMessage(Message message, ClientHandler sender) {
        clients.values().stream()
            .filter(client -> !client.equals(sender))
            .forEach(client -> sendMessage(message, client));
    }

    public void sendMessage(Message message, ClientHandler clientHandler) {
        try {
            ObjectOutputStream out = clientHandler.getOut();
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }

    }

    public void addClient(String username, ClientHandler clientHandler, ConnectionMessage message) {
        if (isUsernameTaken(username)) {
            sendMessage(new ConnectionResponseMessage(ConnectionResponseType.REJECTED), clientHandler);
        } else {
            clients.put(username, clientHandler);
            sendMessage(new ConnectionResponseMessage(ConnectionResponseType.ESTABLISHED), clientHandler);
            broadcastMessage(message, clientHandler);
        }

    }

    public void removeClient(String username, ClientHandler clientHandler, ConnectionMessage message) {
        if (clients.containsKey(username)) {
            clients.remove(username);
            broadcastMessage(message, clientHandler);
        }

    }

    public boolean isUsernameTaken(String username) {
        return clients.containsKey(username);
    }


}
