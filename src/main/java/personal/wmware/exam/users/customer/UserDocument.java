package personal.wmware.exam.users.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserDocument {
    String id;
    String username;
    String password;
}
