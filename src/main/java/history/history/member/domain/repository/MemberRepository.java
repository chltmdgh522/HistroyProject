package history.history.member.domain.repository;

import history.history.member.domain.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Repository
public class MemberRepository {

    private final BCryptPasswordEncoder passwordEncoder;

    private final NamedParameterJdbcTemplate template;

    MemberRepository(DataSource dataSource, BCryptPasswordEncoder passwordEncoder) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        this.passwordEncoder = passwordEncoder;
    }

    public Member save(Member member) {

        if (member.getLoginId().equals("chltmdgh522")) {
            member.setRole("O");
        } else {
            member.setRole("X");
        }

        String sql = "insert into member(id,login_id, password,name,gender,description,email,point,role,profile) " +
                "values(:id,:loginId,:password,:name,:gender,:description,:email,:point,:role,:profile)";


        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", member.getId())
                .addValue("loginId", member.getLoginId())
                .addValue("password", member.getPassword())
                .addValue("name", member.getName())
                .addValue("gender", "남자")//member.getMemberType().getDescription()
                .addValue("description", "안녕하세요 반가워요")
                .addValue("email", member.getEmail())
                .addValue("point", member.getPoint())
                .addValue("role", member.getRole())
                .addValue("profile", member.getProfile());
        template.update(sql, param);
        return member;
    }

    public void updateSession(String session, String id) {
        String sql = "update member set session=:session where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("session", session)
                .addValue("id", id);

        template.update(sql, param);
    }

    public void updateDescriptionMemberNameProfile(String id, Member member) {
        String sql = "update member set description=:description, name=:name, profile=:profile" +
                " where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("description", member.getDescription())
                .addValue("name", member.getName())
                .addValue("profile", member.getProfile())
                .addValue("id", id);

        template.update(sql, param);
    }

    public void updatePoint(String id, Integer point) {
        String sql = "update member set point=:point where id=:id";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("point", point);
        template.update(sql, param);
    }

    public void updateTotalGivePoint(String id, Integer point) {
        String sql = "update member set total_give_point=:point where id=:id";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("point", point);
        template.update(sql, param);
    }

    public void updatePassword(String id, String password) {
        String sql = "update member set password=:password where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("password", passwordEncoder.encode(password))
                .addValue("id", id);

        template.update(sql, param);
    }

    public Optional<Member> findByMemberId(String id) {
        String sql = "select * from member where id=:id";

        try {
            Map<String, String> param = Map.of("id", id);
            Member member = template.queryForObject(sql, param, memberRowMapper());
            log.info("repository={}", member.getProfile());
            return Optional.of(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    public Optional<Member> findByLoginId(String loginId) {
        String sql = "select * from member where login_id=:loginId";

        try {
            Map<String, String> param = Map.of("loginId", loginId);
            Member member = template.queryForObject(sql, param, memberRowMapper());
            return Optional.of(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Member> findByEmail(String email) {
        String sql = "select * from member where email=:email";

        try {
            Map<String, String> param = Map.of("email", email);
            Member member = template.queryForObject(sql, param, memberRowMapper());
            return Optional.of(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Member> findTotalGivePoint() {
        String sql = "select * from member order by total_give_point desc limit 5";
        List<Member> member = template.query(sql, memberRowMapper());
        return member;
    }

    public List<Member> findAll() {
        String sql = "select * from member";
        List<Member> member = template.query(sql, memberRowMapper());
        return member;
    }

    public void delete(Optional<Member> member) {
        String sql = "delete from member where login_id=:loginId";
        SqlParameterSource param = new BeanPropertySqlParameterSource(member);
        template.update(sql, param);
    }


    RowMapper<Member> memberRowMapper() {
        return BeanPropertyRowMapper.newInstance(Member.class);
    }

}
