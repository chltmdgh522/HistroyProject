package history.history.comment.domain.repository.jdbctemplate;

import history.history.comment.domain.Comment;

import java.util.List;

public interface CommentRepository {
    void save(Comment comment);

    List<Comment> findByBoardId(Long boardId);

    void deleteBoard(String boardId);

    void delete(Long commentId);
}
