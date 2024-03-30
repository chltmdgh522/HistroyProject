package history.history.admin.domain.repository;

import history.history.admin.domain.member.JpaMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMemberRepository extends JpaRepository<JpaMember, String> {
    Page<JpaMember> findAll(Pageable pageable);

    Page<JpaMember> findByLoginIdContaining(String loginId, Pageable pageable);

}
