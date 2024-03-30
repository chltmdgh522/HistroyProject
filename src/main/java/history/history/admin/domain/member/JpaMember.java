package history.history.admin.domain.member;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity(name = "Member")
@Data
public class JpaMember {

    @Id
    private String id;

    private String email;

    private String loginId; //로그인 ID

    private String name; //사용자 이름

    private int point;

    private int totalGivePoint;

    private String date;


    public JpaMember() {

    }
}
