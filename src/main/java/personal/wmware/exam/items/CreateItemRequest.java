package personal.wmware.exam.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CreateItemRequest {
    @JsonProperty("item")
    private ItemModel itemModel;
    private String catalog;
}
