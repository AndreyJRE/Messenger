package client;

import common.message.ChatMessage;
import common.message.ConnectionMessage;
import common.message.ConnectionType;
import common.message.Message;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

class WriteThread extends Thread {

    private final ObjectOutputStream writer;

    private final Socket socket;

    private final ChatClient client;

    public WriteThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            writer = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String username = getUsername(scanner);
        String text;
        while (true) {
            text = scanner.nextLine();
            if (text.equalsIgnoreCase("exit")) {
                break;
            }
            ChatMessage message = new ChatMessage(username, text);
            System.out.println("[" + message.getTimestamp() + "] " + "You: " + text);
            sendMessage(message);
        }

        try {
            sendMessage(new ConnectionMessage(username, ConnectionType.EXIT));
            socket.close();
        } catch (IOException e) {
            System.out.println("Error by socket closing" + e.getMessage());
        }


    }

    private String getUsername(Scanner scanner) {
        String username = "";
        while (!client.isConnectedToChat()) {
            System.out.println("Enter your username:");
            username = scanner.nextLine();
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
}