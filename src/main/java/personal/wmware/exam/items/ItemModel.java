package personal.wmware.exam.items;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemModel {
    private String id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Float price;
    @NotNull
    private Integer stock;
    @JsonProperty("discount_settings")
    private DiscountSettings discountSettings;
    private Float priceAfterDiscount;

    public void setPriceAfterDiscount(int amountBought) {
        float updatedPrice = this.getPrice();
        if (Objects.nonNull(discountSettings)
                && (this.discountSettings.getQuantity() >= this.getStock()
                || amountBought >= this.discountSettings.getBundling())) {
            updatedPrice = updatedPrice - (updatedPrice / discountSettings.getDiscountPercent());
        }
        this.priceAfterDiscount = updatedPrice * amountBought;
    }
}
