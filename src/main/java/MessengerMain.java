import client.ChatClient;
import common.exception.NotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;
import server.ChatServer;

public class MessengerMain {

    private static final String HOST = "localhost";

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Do you want to start the new chat or join existing one? (start/join)");
            String answer = scanner.next();
            if (answer.equalsIgnoreCase("start")) {
                handleStart(scanner);
                break;
            } else if (answer.equalsIgnoreCase("join")) {
                handleJoin(scanner);
                break;
            } else {
                System.out.println("Invalid input");
            }
        }

    }

    private static void handleJoin(Scanner scanner) {
        while (true) {
            Integer port = getPort(scanner);
            if (port == null) {
                continue;
            }
            ChatClient client = new ChatClient("localhost", port);
            try {
                client.execute();
                break;
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
            }

        }
    }

    private static void handleStart(Scanner scanner) throws InterruptedException {
        while (true) {
            Integer port = getPort(scanner);
            if (port == null) {
                continue;
            }
            if (isPortAvailable(port)) {
                ChatServer server = new ChatServer(port);
                new Thread(server).start();
                Thread.sleep(500);
                ChatClient client = new ChatClient("localhost", port);
                client.execute();
                break;
            } else {
                System.out.println("Port is already in use, please choose another one.");
            }
        }
    }

    private static Integer getPort(Scanner scanner) {
        System.out.println("Enter the port: ");
        String portString = scanner.next();
        if (!portString.matches("\\d+")) {
            System.out.println("Invalid port number");
            return null;
        }
        return Integer.parseInt(portString);
    }

    private static boolean isPortAvailable(int port) throws IllegalStateException {
        try (Socket ignored = new Socket(HOST, port)) {
            return false;
        } catch (ConnectException e) {
            return true;
        } catch (IOException e) {
            throw new IllegalStateException("Error while trying to check open port", e);
        }
    }

}
