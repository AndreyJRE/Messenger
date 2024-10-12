package common.message;

import lombok.Getter;

@Getter
public class ChatMessage extends Message {

    private final String username;

    private final String content;

    public ChatMessage(String username, String content) {
        this.username = username;
        this.content = content;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + username + ": " + content;
    }
}