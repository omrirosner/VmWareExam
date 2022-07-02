package personal.wmware.exam.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Builder
public class CreateStoreUserRequest {
    @NotNull
    private String storeId;
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private UserType userType;
}
