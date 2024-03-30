package history.history.board.domain.repository.mybatis;

import history.history.board.domain.board.Board;
import history.history.board.domain.repository.BoardRepository;
import history.history.board.domain.repository.BoardSearchCond;
import history.history.member.domain.mypage.MyPageMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

//@Repository
@Slf4j
@RequiredArgsConstructor
public class MyBatisBoardRepository implements BoardRepository {

    private final BoardMapper boardMapper;

    @Override
    public void save(Board board) {
        boardMapper.save(board);
    }

    @Override
    public void updateMemberName(String memberId, MyPageMember member) {
        boardMapper.updateMemberName(memberId, member.getName());
    }

    @Override
    public void updateTitleAndContent(Board board) {
        boardMapper.updateTitleAndContent(board);
    }

    @Override
    public void updateViewCount(Long id, int viewCount) {
        boardMapper.updateViewCount(id, viewCount
        );
    }

    @Override
    public void updateBoardPoint(Long id, int givePoint) {

    }


    @Override
    public Optional<Board> findByMemberIdAndBoardId(String memberId, String boardId) {

        Optional<Board> board = boardMapper.findByMemberIdAndBoardId(memberId, boardId);
        return board;

    }

    @Override
    public Optional<Board> findById(Long id) {
        Optional<Board> board = boardMapper.findById(id);
        return board;
    }

    @Override
    public List<Board> findAll() {
        return boardMapper.findAll();
    }

    @Override
    public List<Board> findSearchAll(BoardSearchCond boardSearchCond) {
        return boardMapper.findSearchAll(boardSearchCond);
    }

    @Override
    public void delete(Board board) {
        boardMapper.delete(board);
    }
}
