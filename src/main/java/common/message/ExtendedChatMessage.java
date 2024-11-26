package common.message;

import lombok.Getter;

@Getter
public class ExtendedChatMessage extends ChatMessage{

    private final String pseudo;

    public ExtendedChatMessage(String username, String content, String pseudo) {
        super(username, content);
        this.pseudo = pseudo;
    }
}
