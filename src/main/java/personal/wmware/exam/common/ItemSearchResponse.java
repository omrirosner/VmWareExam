package personal.wmware.exam.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import personal.wmware.exam.items.ItemModel;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemSearchResponse {
    private List<ItemModel> results;
}
