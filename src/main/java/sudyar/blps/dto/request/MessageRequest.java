package sudyar.blps.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageRequest {

    private String toUser;

    private String description;
}
