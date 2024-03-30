package history.history.member.domain.password;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ForgotPassword {

    @NotEmpty
    @Size(max = 16, message = "15자이하여야 합니다.")
    private String loginId;

    @NotEmpty
    @Email(message = "이메일 형식이 잘못됐습니다.")
    private String email;

}
