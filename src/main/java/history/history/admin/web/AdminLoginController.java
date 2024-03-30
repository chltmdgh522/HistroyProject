package history.history.admin.web;


import history.history.admin.domain.service.AdminService;
import history.history.member.domain.member.Member;
import history.history.member.web.login.loginform.LoginForm;
import history.history.member.web.session.SessionConst;
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

import java.util.Optional;

@Controller
@RequestMapping("/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminLoginController {
    private final AdminService adminService;
    private final VisitService visitService;

    @GetMapping("/login")
    public String adminLogin(@ModelAttribute("member") LoginForm member,
                             Model model,
                             @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember) {

        if (loginMember != null) {
            return "redirect:/";
        }
        Optional<Visitant> visit = visitService.addService();
        model.addAttribute("visit", visit);
        model.addAttribute("loginMember", loginMember);
        return "admin/adminLogin";
    }

    @PostMapping("/login")
    public String adminLoginProcess(@Validated @ModelAttribute("member") LoginForm member,
                                    BindingResult bindingResult,
                                    HttpServletRequest request,Model model) {
        Optional<Visitant> visit = visitService.addService();
        if (bindingResult.hasErrors()) {
            model.addAttribute("visit", visit);
            return "admin/adminLogin";
        }
        Member adminLogin = adminService.adminLogin(member.getLoginId(), member.getPassword());
        if (adminLogin == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            model.addAttribute("visit", visit);
            return "admin/adminLogin";
        }
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, adminLogin);


        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
