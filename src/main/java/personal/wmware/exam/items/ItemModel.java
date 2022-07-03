package personal.wmware.exam.items;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ItemModel {
    private String name;
    private String description;
    private Float price;
    private Integer stock;
}
