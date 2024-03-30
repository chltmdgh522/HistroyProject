package history.history.board.domain.repository.jdbcTemplate;

import history.history.board.domain.board.Board;
import history.history.board.domain.repository.BoardRepository;
import history.history.board.domain.repository.BoardSearchCond;
import history.history.member.domain.mypage.MyPageMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
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
import java.util.Optional;

@Slf4j
@Repository
public class JdbcTemplateBoardRepository implements BoardRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    JdbcTemplateBoardRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void save(Board board) {

        String sql = "insert into board(board_id,title,content, member_id,member_name,board_image,date,board_type,option_point) " +
                "values(:boardId,:title,:content,:memberId,:memberName,:boardImage,:date,:boardType,:optionPoint)";
        SqlParameterSource param = new BeanPropertySqlParameterSource(board);
        KeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, key);
        board.setId(key.getKey().longValue());

    }

    @Override
    public void updateMemberName(String memberId, MyPageMember member) {
        String sql = "update board set member_name=:memberName where member_id=:memberId";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("memberName", member.getName())
                .addValue("memberId", memberId);

        jdbcTemplate.update(sql, param);
    }

    @Override
    public void updateTitleAndContent(Board board) {
        String sql = "update board set title=:title, content=:content, modify=:update, board_image=:boardImage where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("title", board.getTitle())
                .addValue("content", board.getContent())
                .addValue("update", board.getModify())
                .addValue("boardImage", board.getBoardImage())
                .addValue("id", board.getId());

        jdbcTemplate.update(sql, param);
    }

    @Override
    public void updateViewCount(Long id, int viewCount) {
        String sql = "update board set view_count=:viewCount where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("viewCount", viewCount)
                .addValue("id", id);
        jdbcTemplate.update(sql, param);
    }

    @Override
    public void updateBoardPoint(Long id, int givePoint) {
        String sql = "update board set give_point=:givePoint where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("givePoint", givePoint)
                .addValue("id", id);
        jdbcTemplate.update(sql, param);
    }

    @Override
    public Optional<Board> findByMemberIdAndBoardId(String memberId, String boardId) {
        String sql = "select * from board where member_id=:memberId and board_id=:boardId";

        try {
            Map<String, String> param = Map.of("memberId", memberId, "boardId", boardId);
            Board board = jdbcTemplate.queryForObject(sql, param, boardRowMapper());
            return Optional.of(board);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Board> findById(Long id) {
        String sql = "select * from board where id=:id";
        try {
            Map<String, Long> param = Map.of("id", id);
            Board board = jdbcTemplate.queryForObject(sql, param, boardRowMapper());
            return Optional.of(board);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Board> findAll() {
        String sql = "select * from board";
        return jdbcTemplate.query(sql, boardRowMapper());
    }

    @Override
    public List<Board> findSearchAll(BoardSearchCond boardSearchCond) {

        return null;
    }

    @Override
    public void delete(Board board) {
        String sql = "delete from board where id=:id";
        SqlParameterSource param = new BeanPropertySqlParameterSource(board);
        jdbcTemplate.update(sql, param);
    }

    RowMapper<Board> boardRowMapper() {
        return BeanPropertyRowMapper.newInstance(Board.class);
    }
}


