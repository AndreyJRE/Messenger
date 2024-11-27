package client;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import common.exception.UnknownMessageException;
import common.message.ConnectionResponseMessage;
import common.message.ConnectionResponseType;
import common.message.Message;
import java.net.Socket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReadThreadTest {

    @Mock
    Socket socket;

    @Mock
    ChatClient chatClient;

    ReadThread readThread;

    @BeforeEach
    void setUp() {
        readThread = new ReadThread(socket, chatClient);
    }


    @Test
    void testHandleMessage_ConnectionResponseMessage_Established() {
        ConnectionResponseMessage responseMessage = new ConnectionResponseMessage(ConnectionResponseType.ESTABLISHED);
        readThread.handleMessage(responseMessage);
        verify(chatClient, times(1)).setConnectedToChat(true);
    }

    @Test
    void testHandleMessage_ConnectionResponseMessage_Rejected() {
        ConnectionResponseMessage responseMessage = new ConnectionResponseMessage(ConnectionResponseType.REJECTED);
        readThread.handleMessage(responseMessage);
        verify(chatClient, never()).setConnectedToChat(true);
    }


    @Test
    void testHandleMessage_unknownMessage() {
        Assertions.assertThrows(UnknownMessageException.class, () -> readThread.handleMessage(new Message() {
            @Override
            public String getTimestamp() {
                return super.getTimestamp();
            }
        }));
    }
}