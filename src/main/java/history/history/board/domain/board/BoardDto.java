package history.history.board.domain.board;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BoardDto {

    @Size(max = 13, message = "12자 이하여야 됩니다.")
    String title;

    String content;

    boolean boardType;

    MultipartFile boardImage;

    int optionPoint;
}
