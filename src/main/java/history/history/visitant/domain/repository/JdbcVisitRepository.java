package history.history.visitant.domain.repository;

import history.history.visitant.domain.visit.Visitant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class JdbcVisitRepository implements VisitRepository {

    NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcVisitRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void updateTotal(int total) {
        String sql = "update visitant set total =:total where id=1";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("total", total);

        jdbcTemplate.update(sql, param);
    }

    @Override
    public void updateToday(int today) {
        String sql = "update visitant set today =:today where id=1";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("today", today);

        jdbcTemplate.update(sql, param);
    }

    @Override
    public Optional<Visitant> findVisit(int id) {
        String sql = "select * from visitant where id=:id";
        Map<String, Integer> param = Map.of("id", id);
        Visitant visitant = jdbcTemplate.queryForObject(sql, param, visitantRowMapper());

        return Optional.ofNullable(visitant);
    }

    RowMapper<Visitant> visitantRowMapper() {
        return BeanPropertyRowMapper.newInstance(Visitant.class);
    }
}
