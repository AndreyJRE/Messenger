import client.ChatClient;
import common.exception.NotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;
import server.ChatServer;

public class MessengerMain {

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to start the new chat or join existing one? (start/join)");
        String answer = scanner.next();
        if (answer.equalsIgnoreCase("start")) {
            while (true) {
                System.out.println("Enter the port: ");
                int port = scanner.nextInt();
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
        } else if (answer.equalsIgnoreCase("join")) {
            while (true) {
                System.out.println("Enter the port: ");
                int port = scanner.nextInt();
                ChatClient client = new ChatClient("localhost", port);
                try {
                    client.execute();
                    break;
                } catch (NotFoundException  e) {
                    System.out.println(e.getMessage());
                }

            }
        } else {
            System.out.println("Invalid input");
        }
    }

    private static boolean isPortAvailable(int port) throws IllegalStateException {
        try (Socket ignored = new Socket("localhost", port)) {
            return false;
        } catch (ConnectException e) {
            return true;
        } catch (IOException e) {
            throw new IllegalStateException("Error while trying to check open port", e);
        }
    }

}
