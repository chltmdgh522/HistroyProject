package history.history.give.domain.reposiotry;

import history.history.give.domain.give.Give;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class GiveJdbcRepository implements GiveRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public GiveJdbcRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void save(Give give) {
        String sql = "insert into give(give_point,member_id,board_id,give_text,name,email) " +
                "values(:givePoint,:memberId,:boardId,:giveText,:name,:email)";
        SqlParameterSource param = new BeanPropertySqlParameterSource(give);
        KeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, key);
    }

    @Override
    public Optional<Give> findByMemberId(String memberId) {
        String sql = "select * from give where member_id=:memberId";
        Map<String, String> param = Map.of("memberId", memberId);
        Give give = jdbcTemplate.queryForObject(sql, param, giveRowMapper());
        return Optional.ofNullable(give);
    }

    @Override
    public List<Give> findByBoardId(Long boardId) {
        String sql = "select * from give where board_id=:boardId";
        Map<String, Long> param = Map.of("boardId", boardId);
        return jdbcTemplate.query(sql, param, giveRowMapper());
    }

    RowMapper<Give> giveRowMapper() {
        return new BeanPropertyRowMapper<>(Give.class);
    }
}
