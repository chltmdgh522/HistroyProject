package history.history.member.domain.login;

import history.history.member.domain.member.Member;
import history.history.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Member login(String loginId, String password) {

        return memberRepository.findByLoginId(loginId)
                .filter(m -> bCryptPasswordEncoder.matches(password, m.getPassword()))
                .orElse(null);
    }

    public void point(Member loginMember) {
        Integer point = loginMember.getPoint() + 100;
        memberRepository.updatePoint(loginMember.getId(), point);
    }
}


