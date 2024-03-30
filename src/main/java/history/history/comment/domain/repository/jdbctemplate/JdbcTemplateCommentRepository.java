package history.history.comment.domain.repository.jdbctemplate;

import history.history.comment.domain.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class JdbcTemplateCommentRepository implements CommentRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcTemplateCommentRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public void save(Comment comment) {
        String sql = "insert into comment (content, member_id, board_id, member_name) " +
                "values (:content,:memberId,:boardId,:memberName)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource param = new BeanPropertySqlParameterSource(comment);

        jdbcTemplate.update(sql, param, keyHolder);
    }

    public List<Comment> findByBoardId(Long boardId) {
        String sql = "select * from comment where board_id=:id";
        Map<String, Long> param = Map.of("id", boardId);
        List<Comment> comment = jdbcTemplate.query(sql, param, commentRowMapper());

        return comment;
    }

    //게시판 한번에 삭제할려고 왜냐면 외래키 때문에 오류남
    public void deleteBoard(String boardId) {
        String sql = "delete from comment where board_id=:id";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", boardId);
        jdbcTemplate.update(sql, param);
    }


    public void delete(Long commentId) {
        String sql = "delete from comment where id=:id";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", commentId);
        jdbcTemplate.update(sql, param);
    }

    RowMapper<Comment> commentRowMapper() {
        return BeanPropertyRowMapper.newInstance(Comment.class);
    }
}
