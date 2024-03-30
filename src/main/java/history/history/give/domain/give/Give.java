package history.history.give.domain.give;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class Give {

    Long id;

    int givePoint;

    String memberId;

    String name;

    String email;

    Long boardId;

    @NotEmpty
    String giveText;

    String date;
}
