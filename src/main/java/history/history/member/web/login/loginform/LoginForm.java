package history.history.member.web.login.loginform;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class LoginForm {

    @NotEmpty
    private String loginId;
    @NotEmpty
    private String password;
}
