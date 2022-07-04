package personal.wmware.exam.common.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ActionResponse {
    private String message;
    private boolean success;
}
