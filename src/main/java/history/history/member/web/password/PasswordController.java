package history.history.member.web.password;

import history.history.member.domain.member.Member;
import history.history.member.domain.password.ChangePassword;
import history.history.member.domain.password.ForgotPassword;
import history.history.member.domain.repository.MemberRepository;
import history.history.member.domain.service.MemberService;
import history.history.member.web.session.SessionConst;
import history.history.visitant.domain.service.VisitService;
import history.history.visitant.domain.visit.Visitant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PasswordController {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final MemberService memberService;

    private final VisitService visitService;


    private final MemberRepository memberRepository; //원래 이렇게 하면 안됨,,,,

    @GetMapping("/change-password")
    public String changePassword(@ModelAttribute("password") ChangePassword password,
                                 @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                 Model model) {
        Optional<Visitant> visit = visitService.addService();
        model.addAttribute("visit", visit);
        model.addAttribute("loginMember", loginMember);
        return "password/change-password";
    }

    @PostMapping("/change-password")
    public String postPassword(@Validated @ModelAttribute("password") ChangePassword password,
                               BindingResult bindingResult,
                               @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                               HttpServletRequest request,
                               Model model) {
        Optional<Visitant> visit = visitService.addService();
        if (bindingResult.hasErrors()) {
            model.addAttribute("visit", visit);
            return "password/change-password";
        }
        Member member = memberService.findByMemberId(loginMember.getId());
        if (!bCryptPasswordEncoder.matches(password.getOriginalPassword(), member.getPassword())) {
            bindingResult.reject("err", "기존 비밀번호가 맞지 않습니다.");
            model.addAttribute("visit", visit);
            return "password/change-password";
        }

        if (!Objects.equals(password.getNewPassword(), password.getNewReturnPassword())) {
            bindingResult.reject("err", "새 비빌번호가 일치하지 않습니다.");
            model.addAttribute("visit", visit);

            return "password/change-password";
        }

        if (bCryptPasswordEncoder.matches(password.getNewPassword(), member.getPassword())) {
            bindingResult.reject("err", "새 비밀번호가 기존 비밀번호랑 일치합니다.");
            model.addAttribute("visit", visit);

            return "password/change-password";
        }

        memberService.updatePassword(loginMember.getId(), password.getNewPassword());

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String getForgotPassword(@ModelAttribute("forgotPassword") ForgotPassword forgotPassword,
                                    @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                    Model model) {

        if (loginMember != null) {
            return "redirect:/";
        }
        Optional<Visitant> visit = visitService.addService();
        model.addAttribute("visit", visit);
        model.addAttribute("loginMember", loginMember);
        return "password/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String postForgotPassword(@Validated @ModelAttribute("forgotPassword") ForgotPassword forgotPassword,
                                     BindingResult bindingResult,
                                     HttpServletRequest request, Model model) {
        Optional<Visitant> visit = visitService.addService();
        if (bindingResult.hasErrors()) {
            model.addAttribute("visit", visit);
            return "password/forgot-password";
        }
        String idEmail = memberService.findIdEmail(forgotPassword);
        if (Objects.equals(idEmail, "loginId")) {
            bindingResult.reject("err", "존재하지 않은 아이디입니다.");
            model.addAttribute("visit", visit);

            return "password/forgot-password";
        }
        if (Objects.equals(idEmail, "email")) {
            bindingResult.reject("err", "존재하지 않은 이메일입니다.");
            model.addAttribute("visit", visit);

            return "password/forgot-password";
        }


        HttpSession session = request.getSession(); //디폴트가 true
        //세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.TEM_MEMBER, forgotPassword.getLoginId());


        return "redirect:/tem";
    }


    @GetMapping("/tem")
    public String getTem(@ModelAttribute("forgotPassword") ForgotPassword forgotPassword,
                         @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                         @SessionAttribute(name = SessionConst.TEM_MEMBER, required = false) String loginMember2,
                         Model model,
                         HttpServletRequest request) {

        if (loginMember != null || loginMember2 == null) {
            return "redirect:/";
        }

        Random random = new Random();
        int randomNumber = 1000 + random.nextInt(9000);

        Optional<Member> member = memberRepository.findByLoginId(loginMember2);
        memberService.updatePassword(member.get().getId(), String.valueOf(randomNumber));
        model.addAttribute("password", randomNumber);

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }
        Optional<Visitant> visit = visitService.addService();
        model.addAttribute("visit", visit);
        model.addAttribute("loginMember", loginMember);
        return "password/tem";
    }
}
