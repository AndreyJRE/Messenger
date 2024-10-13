package client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import common.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatClientTest {

    @Spy
    ChatClient chatClient = new ChatClient("localhost", 12345);

    @Test
    void testExecute_PortNotAvailable() {
        try {
            chatClient.execute();
            fail("Expected NotFoundException to be thrown");
        } catch (NotFoundException e) {
            assertEquals("Port " + 12345 + " is not available", e.getMessage());
        }
    }

    @Test
    void testSetConnectedToChatTrue() {
        chatClient.setConnectedToChat(true);
        assertTrue(chatClient.isConnectedToChat());
    }

    @Test
    void testSetConnectedToChatFalse() {
        chatClient.setConnectedToChat(false);
        assertFalse(chatClient.isConnectedToChat());
    }
}