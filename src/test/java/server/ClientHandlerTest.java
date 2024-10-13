package server;


import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import common.message.ChatMessage;
import common.message.ConnectionMessage;
import common.message.ConnectionType;
import common.message.Message;
import java.io.IOException;
import java.net.Socket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ClientHandlerTest {


    ClientHandler clientHandler;

    ChatServer chatServer;

    Socket socket;


    @BeforeEach
    void setUp()  {
        chatServer = mock(ChatServer.class);
        socket = mock(Socket.class);
        clientHandler = new ClientHandler(socket, chatServer);
        spy(clientHandler);
    }

    @Test
    void testHandleIncomingMessage_ChatMessage() throws Exception {
        ChatMessage chatMessage = new ChatMessage("user", "Hello");

        doNothing().when(chatServer).broadcastMessage(chatMessage, clientHandler);

        clientHandler.handleIncomingMessage(chatMessage);

        verify(chatServer, times(1)).broadcastMessage(chatMessage, clientHandler);
    }

    @Test
    void testHandleIncomingMessage_ConnectionMessageJoin()  {
        ConnectionMessage connectionMessage = new ConnectionMessage("user", ConnectionType.JOIN);
        doNothing().when(chatServer).addClient("user", clientHandler, connectionMessage);

        clientHandler.handleConnectionMessage(connectionMessage);

        verify(chatServer, times(1)).addClient("user", clientHandler, connectionMessage);
    }

    @Test
    void testHandleIncomingMessage_ConnectionMessageExit() {
        ConnectionMessage connectionMessage = new ConnectionMessage("user", ConnectionType.EXIT);
        doNothing().when(chatServer).removeClient("user", clientHandler, connectionMessage);

        clientHandler.handleConnectionMessage(connectionMessage);

        verify(chatServer, times(1)).removeClient("user", clientHandler, connectionMessage);

    }

    @Test
    void testHandleIncomingMessage_UnknownMessage() throws Exception {
        Message unknownMessage = mock(Message.class);
        clientHandler.handleIncomingMessage(unknownMessage);

        verify(socket, times(1)).close();
    }
}
