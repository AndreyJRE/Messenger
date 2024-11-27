package client;

import common.exception.NotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ChatClient {

    private final String hostname;

    private final int port;

    private AtomicBoolean connectedToChat = new AtomicBoolean(false);

    private WriteThread writeThread;

    private ReadThread readThread;

    private Socket socket;

    public void execute() {
        try {
            socket = new Socket(hostname, port);
            System.out.println("Connected to the server on port " + port);
            writeThread = new WriteThread(socket, this);
            readThread = new ReadThread(socket, this);
            readThread.start();
            writeThread.start();

        } catch (IOException ex) {
            throw new NotFoundException("Port " + port + " is not available");
        }
    }

    public boolean isConnectedToChat() {
        return connectedToChat.get();
    }

    public void setConnectedToChat(boolean connectedToChat) {
        this.connectedToChat.set(connectedToChat);
    }

    public void disconnect() {
        connectedToChat.set(false);
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error by socket closing" + e.getMessage());
            }
        }
        writeThread.closeInputStream();

    }

}




