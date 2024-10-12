package common.message;

import lombok.Getter;

@Getter
public class ConnectionMessage extends Message {

    private final ConnectionType type;

    private final String username;

    public ConnectionMessage(String username, ConnectionType type) {
        this.username = username;
        this.type = type;
    }

    @Override
    public String toString() {
        if (type == ConnectionType.JOIN) {
            return "[" + timestamp + "] " + username + " joined the chat.";
        } else if (type == ConnectionType.EXIT) {
            return "[" + timestamp + "] " + username + " left the chat.";
        }
        return "Unknown connection message type: " + type;
    }
}
