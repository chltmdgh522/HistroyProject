package history.history.board.domain.board;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class  Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //DB에서 값을 넣어주는거
    Long id;

    String boardId;

    String title;

    String content;

    String memberName;

    String memberId;

    int viewCount;

    String date;

    String modify;

    boolean boardType;

    int givePoint;

    String boardImage;

    int optionPoint;

    public Board() {

    }
}
