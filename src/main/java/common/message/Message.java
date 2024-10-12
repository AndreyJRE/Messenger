package common.message;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import lombok.Getter;

@Getter
public abstract class Message implements Serializable {

    protected final String timestamp;

    public Message() {
        this.timestamp = new SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
    }

}
