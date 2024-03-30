package history.history.board.domain.repository.jpa;

import history.history.board.domain.board.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaBoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findAllByBoardTypeFalse(Pageable pageable);

    Page<Board> findAll(Pageable pageable);

    Page<Board> findByMemberNameContainingAndTitleContainingAndBoardTypeFalse(
            String memberName, String title, Pageable pageable);
}

