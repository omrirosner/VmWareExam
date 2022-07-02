package personal.wmware.exam.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ActionResponse {
    private String message;
    private boolean success;
}
