package history.history.admin.domain.repository;

import history.history.admin.domain.comment.JpaComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCommentRepository extends JpaRepository<JpaComment, Long> {
    Page<JpaComment> findAll(Pageable pageable);

    Page<JpaComment> findByMemberNameContainingAndContentContaining(String name, String content, Pageable pageable);

}
