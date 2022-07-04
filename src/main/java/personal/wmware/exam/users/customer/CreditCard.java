package personal.wmware.exam.users.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditCard {
    @JsonProperty("number")
    @NotNull
    private String number;
    @NotNull
    private String expDate;
    @NotNull
    @Max(999)
    @Min(100)
    private int cvv;
}
