package server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import common.message.ConnectionMessage;
import common.message.ConnectionResponseMessage;
import common.message.ConnectionType;
import common.message.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatServerTest {

    @Spy
    ChatServer chatServer = new ChatServer(12345);

    @Mock
    private ClientHandler clientHandler;


    @Test
    void testAddClient() {
        String username = "testUser";
        ConnectionMessage message = new ConnectionMessage(username, ConnectionType.JOIN);
        doNothing().when(chatServer).sendMessage(any(ConnectionResponseMessage.class), eq(clientHandler));

        chatServer.addClient(username, clientHandler, message);

        verify(chatServer, times(1)).sendMessage(any(ConnectionResponseMessage.class), eq(clientHandler));
        verify(chatServer, times(1)).broadcastMessage(message, clientHandler);
        assertTrue(chatServer.getClients().containsKey(username));
    }

    @Test
    void testRemoveClient() {
        String username = "testUser";
        ConnectionMessage message = new ConnectionMessage(username, ConnectionType.EXIT);
        doNothing().when(chatServer).sendMessage(any(ConnectionResponseMessage.class), eq(clientHandler));

        chatServer.addClient(username, clientHandler, message);
        chatServer.removeClient(username, clientHandler, message);

        verify(chatServer, times(1)).sendMessage(any(ConnectionResponseMessage.class), eq(clientHandler));
        verify(chatServer, times(2)).broadcastMessage(any(), any());
        assertFalse(chatServer.getClients().containsKey(username));
    }

    @Test
    void testBroadcastMessage() {
        String username1 = "user1";
        String username2 = "user2";
        Message message = mock(Message.class);
        ClientHandler clientHandler1 = mock(ClientHandler.class);
        ClientHandler clientHandler2 = mock(ClientHandler.class);
        doNothing().when(chatServer).sendMessage(any(), any());

        chatServer.addClient(username1, clientHandler1, new ConnectionMessage(username1, ConnectionType.JOIN));
        chatServer.addClient(username2, clientHandler2, new ConnectionMessage(username2, ConnectionType.JOIN));

        chatServer.broadcastMessage(message, clientHandler1);

        verify(chatServer, times(1)).sendMessage(message, clientHandler2);

    }

    @Test
    void testIsUsernameTaken() {
        String username = "testUser";
        ConnectionMessage message = new ConnectionMessage(username, ConnectionType.JOIN);
        doNothing().when(chatServer).sendMessage(any(), any());

        chatServer.addClient(username, clientHandler, message);

        assertTrue(chatServer.isUsernameTaken(username));
        assertFalse(chatServer.isUsernameTaken("anotherUser"));
    }
}