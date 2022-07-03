package personal.wmware.exam.users.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDocument {
    public UserDocument(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    private String id;
    private String username;
    private String password;
    private PaymentMethods paymentMethods;
}
