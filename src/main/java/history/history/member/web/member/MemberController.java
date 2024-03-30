package history.history.member.web.member;

import history.history.member.domain.member.Member;
import history.history.member.domain.member.MemberType;
import history.history.member.domain.service.MemberService;
import history.history.member.web.session.SessionConst;
import history.history.visitant.domain.service.VisitService;
import history.history.visitant.domain.visit.Visitant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    private final VisitService visitService;



    @ModelAttribute("memberType")
    public MemberType[] memberType() {
        MemberType[] values = MemberType.values();
        return values;
    }

    @GetMapping("/add")
    public String addForm(@ModelAttribute("member") Member member,
                          @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                          Model model) {

        if (loginMember != null) {
            return "redirect:/";
        }
        Optional<Visitant> visit = visitService.addService();
        model.addAttribute("visit", visit);
        model.addAttribute("loginMember", loginMember);
        return "members/addMemberForm";
    }

    @PostMapping("/add")
    public String save(@Validated @ModelAttribute Member member, BindingResult bindingResult,
                       Model model) throws SQLException {
        Optional<Visitant> visit = visitService.addService();

        if (bindingResult.hasErrors()) {
            model.addAttribute("visit", visit);
            return "members/addMemberForm";
        }
        if (!Objects.equals(member.getPassword(), member.getPasswordCheck())) {
            model.addAttribute("visit", visit);
            bindingResult.reject("addFail", "비밀번호가 일치하지 않습니다.");
            return "members/addMemberForm";
        }

        String save = memberService.save(member);
        if (Objects.equals(save, "loginId")) {
            model.addAttribute("visit", visit);
            bindingResult.reject("addFail", "존재하는 아이디가 있습니다.");
            return "members/addMemberForm";
        }
        if (Objects.equals(save, "email")) {
            model.addAttribute("visit", visit);
            bindingResult.reject("addFail", "존재하는 이메일이 있습니다.");
            return "members/addMemberForm";
        }
        return "redirect:/login";
    }

}
