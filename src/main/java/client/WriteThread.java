package client;

import common.message.ChatMessage;
import common.message.ConnectionMessage;
import common.message.ConnectionType;
import common.message.Message;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

class WriteThread extends Thread {

    private final Socket socket;

    private final ChatClient client;

    private final Scanner scanner;

    private ObjectOutputStream writer;

    public WriteThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        try {
            writer = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error getting output stream: " + e.getMessage());
            throw new RuntimeException(e);
        }

        String username = getUsername();
        String text;
        while (true) {
            try {
                text = scanner.nextLine();
            } catch (NoSuchElementException e) {
                System.out.println("You are disconnected from the chat");
                break;
            }
            if (text.isEmpty()) {
                continue;
            }
            if (text.equalsIgnoreCase("exit")) {
                break;
            }
            ChatMessage message = new ChatMessage(username, text);
            System.out.println("[" + message.getTimestamp() + "] " + "You: " + text);
            sendMessage(message);
        }

        try {
            if (!socket.isClosed()) {
                sendMessage(new ConnectionMessage(username, ConnectionType.EXIT));
                socket.close();
                client.disconnect();
            }
        } catch (IOException e) {
            System.out.println("Error by socket closing" + e.getMessage());
        }


    }

    private String getUsername() {
        String username = "";
        while (!client.isConnectedToChat()) {
            System.out.println("Enter your username:");
            username = scanner.nextLine();
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty");
                continue;
            }
            sendMessage(new ConnectionMessage(username, ConnectionType.JOIN));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return username;
    }

    public void sendMessage(Message message) {
        try {
            writer.writeObject(message);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error writing to server: " + e.getMessage());
        }
    }

    // Handle server disconnection
    public void closeInputStream() {
        try {
            System.in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}