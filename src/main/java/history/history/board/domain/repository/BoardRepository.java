package history.history.board.domain.repository;

import history.history.board.domain.board.Board;
import history.history.member.domain.mypage.MyPageMember;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {

    void save(Board board);

    void updateMemberName(String memberId, MyPageMember member);

    void updateTitleAndContent(Board board);

    void updateViewCount(Long id, int viewCount);

    void updateBoardPoint(Long id, int givePoint);

    Optional<Board> findByMemberIdAndBoardId(String memberId, String boardId);

    Optional<Board> findById(Long id);

    List<Board> findAll();

    List<Board> findSearchAll(BoardSearchCond boardSearchCond);


    void delete(Board board);
}
