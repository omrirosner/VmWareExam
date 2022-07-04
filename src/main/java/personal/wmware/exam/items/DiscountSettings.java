package personal.wmware.exam.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DiscountSettings {
    private Integer quantity;
    private Integer bundling;
    @JsonProperty("discount_percent")
    private Integer discountPercent;
}