package history.history.member.domain.password;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ChangePassword {

    @NotEmpty
    String originalPassword;

    @NotEmpty
    String newPassword;
    @NotEmpty
    String newReturnPassword;

}
