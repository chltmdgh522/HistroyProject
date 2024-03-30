package history.history.admin.domain.comment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity(name = "Comment")
@Data
public class JpaComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //DB에서 값을 넣어주는거
    Long id;

    String content;

    String memberName;

    String date;

    public JpaComment() {

    }
}
