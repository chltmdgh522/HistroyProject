package history.history.board.domain.repository.mybatis;

import history.history.board.domain.board.Board;
import history.history.board.domain.repository.BoardSearchCond;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BoardMapper {
    void save(Board board);

    void updateMemberName(@Param("memberId") String memberId, @Param("memberName") String memberName);

    void updateTitleAndContent(Board board);

    void updateViewCount(@Param("id") Long id, @Param("viewCount") int viewCount);

    Optional<Board> findByMemberIdAndBoardId(@Param("memberId") String memberId, @Param("boardId") String boardId);

    Optional<Board> findById(Long id);

    List<Board> findAll();

    List<Board> findSearchAll(BoardSearchCond boardSearchCond);

    void delete(Board board);
}
