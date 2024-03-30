package history.history.comment.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Comment {


    Long id;
    @NotEmpty
    @Size(max = 255, message = "길이는 최대 255자입니다.")
    String content;

    String memberName;

    String memberId;

    String date;

    Long boardId;

}
