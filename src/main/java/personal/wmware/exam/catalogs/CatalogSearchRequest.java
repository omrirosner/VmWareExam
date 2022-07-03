package personal.wmware.exam.catalogs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogSearchRequest {
    private Integer maxPrice;
    private Integer minPrice;
    private String name;
    private String description;
    private boolean mustBeInStock = false;
}
