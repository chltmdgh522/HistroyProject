package history.history.member.web.session;

import history.history.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {

    private final MemberRepository memberRepository;

    public void sessionSave(String session, String loginId) {
        memberRepository.findByLoginId(loginId).
                ifPresent(member -> memberRepository.updateSession(session, member.getId()));

    }

}
