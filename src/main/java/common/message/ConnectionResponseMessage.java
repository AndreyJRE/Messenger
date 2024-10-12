package common.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConnectionResponseMessage extends Message {

    private final ConnectionResponseType responseType;

}
