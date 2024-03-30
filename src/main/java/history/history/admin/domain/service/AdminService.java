package history.history.admin.domain.service;

import history.history.admin.domain.comment.JpaComment;
import history.history.admin.domain.member.JpaMember;
import history.history.admin.domain.repository.JpaCommentRepository;
import history.history.admin.domain.repository.JpaMemberRepository;
import history.history.member.domain.member.Member;
import history.history.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final JpaMemberRepository jpaMemberRepository;


    private final JpaCommentRepository jpaCommentRepository;

    public Member adminLogin(String loginId, String password) {

        Optional<Member> member = memberRepository.findByLoginId(loginId);
        if (member.isPresent()) {
            if (member.get().getRole().equals("O")) {

                return memberRepository.findByLoginId(loginId)
                        .filter(a -> bCryptPasswordEncoder.matches(password, a.getPassword()))
                        .orElse(null);
            }
        }
        return null;
    }

    //페이징
    public Page<JpaMember> getList(String loginId, int page) {

        Pageable pageable = PageRequest.of(page, 10);

        if (loginId.equals("")) {
            return jpaMemberRepository.findAll(pageable);
        }

        return jpaMemberRepository.findByLoginIdContaining(loginId, pageable);
    }

    public Page<JpaComment> getListComment(String name, String comment, int page) {

        Pageable pageable = PageRequest.of(page, 10);

        if (comment.equals("")) {
            return jpaCommentRepository.findAll(pageable);
        }

        return jpaCommentRepository.findByMemberNameContainingAndContentContaining(name, comment, pageable);
    }

}
