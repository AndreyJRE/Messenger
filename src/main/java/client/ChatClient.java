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

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);
            System.out.println("Connected to the server on port " + port);
            WriteThread writeThread = new WriteThread(socket, this);
            ReadThread readThread = new ReadThread(socket, this);
            readThread.start();
            writeThread.start();

        } catch (IOException ex) {
            throw new NotFoundException("Port " + port + " is not available");
        }
    }

    public void setConnectedToChat(boolean connectedToChat) {
        this.connectedToChat.set(connectedToChat);
    }

    public boolean isConnectedToChat() {
        return connectedToChat.get();
    }

}




