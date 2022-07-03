package personal.wmware.exam.items;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ItemModel {
    private String id;
    private String name;
    private String description;
    private Float price;
    private Integer stock;
}
