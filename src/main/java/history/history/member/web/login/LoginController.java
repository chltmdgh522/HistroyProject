package history.history.member.web.login;


import history.history.member.domain.login.LoginService;
import history.history.member.domain.member.Member;
import history.history.member.web.login.loginform.LoginForm;
import history.history.member.web.session.SessionConst;
import history.history.member.web.session.SessionService;
import history.history.visitant.domain.service.VisitService;
import history.history.visitant.domain.visit.Visitant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final SessionService sessionService;

    private final VisitService visitService;


    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm,
                            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                            Model model) {

        if (loginMember != null) {
            return "redirect:/";
        }


        Optional<Visitant> visit = visitService.addService();
        model.addAttribute("visit", visit);
        model.addAttribute("loginMember", loginMember);
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String loginV4(@Validated @ModelAttribute LoginForm form, BindingResult bindingResult,
                          @RequestParam(defaultValue = "/") String redirectURL,
                          HttpServletRequest request,Model model) {
        Optional<Visitant> visit = visitService.addService();

        if (bindingResult.hasErrors()) {
            model.addAttribute("visit", visit);
            return "/login/loginForm";
        }
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            model.addAttribute("visit", visit);

            return "/login/loginForm";
        }
        if (loginMember.getRole().equals("O")) {

            bindingResult.reject("super", "관리자 계정입니다.");
            model.addAttribute("visit", visit);
            return "/login/loginForm";
        }
        //로그인 성공 처리
        //세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        HttpSession session = request.getSession(); //디폴트가 true
        //세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
        Date date = new Date(session.getLastAccessedTime());
        sessionService.sessionSave(String.valueOf(date), form.getLoginId());

        //포인트 점수 100점
        loginService.point(loginMember);

        return "redirect:" + redirectURL;
    }

    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }


}