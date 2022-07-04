package personal.wmware.exam.items;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class BuyItemRequest {
    @NotNull
    private String itemId;
    private Integer amount = 1;
}
