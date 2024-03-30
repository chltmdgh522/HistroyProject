package history.history.admin.web;

import history.history.admin.domain.member.JpaMember;
import history.history.admin.domain.member.MemberSearch;
import history.history.admin.domain.service.AdminService;
import history.history.member.domain.member.Member;
import history.history.member.domain.repository.MemberRepository;
import history.history.member.web.session.SessionConst;
import history.history.visitant.domain.service.VisitService;
import history.history.visitant.domain.visit.Visitant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminMemberController {
    private final AdminService adminService;
    private final VisitService visitService;

    private final MemberRepository memberRepository;

    //관리자 페이지
    @GetMapping
    public String adminInformation(
            Model model,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember) {
        if (loginMember.getRole().equals("X")) {
            return "redirect:/";
        }
        Optional<Visitant> visit = visitService.addService();
        model.addAttribute("visit", visit);
        model.addAttribute("loginMember",loginMember);

        return "admin/admin";
    }

    @GetMapping("/member")
    public String memberInformation(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                    @ModelAttribute("admin") JpaMember member,
                                    @ModelAttribute("memberSearchCond") MemberSearch cond,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    Model model) {
        if (loginMember.getRole().equals("X")) {
            return "redirect:/";
        }
        Page<JpaMember> list = adminService.getList(cond.getLoginId(), page);
        Optional<Visitant> visit = visitService.addService();
        model.addAttribute("visit", visit);
        model.addAttribute("loginMember",loginMember);
        model.addAttribute("paging", list);
        return "admin/adminMember";
    }

    @PostMapping("/{loginId}/point")
    public String point(@ModelAttribute JpaMember member,
                        @PathVariable String loginId,
                        Model model) {
        log.info("member={}", member.getPoint());
        Optional<Member> fmember = memberRepository.findByLoginId(loginId);
        model.addAttribute("member", member);
        memberRepository.updatePoint(fmember.get().getId(), member.getPoint());
        return "redirect:/admin/member";
    }

    @PostMapping("/{loginId}/givePoint")
    public String givPoint(@ModelAttribute JpaMember member,
                           @PathVariable String loginId,
                           Model model) {
        Optional<Member> fmember = memberRepository.findByLoginId(loginId);
        model.addAttribute("member", member);
        memberRepository.updateTotalGivePoint(fmember.get().getId(), member.getTotalGivePoint());
        return "redirect:/admin/member";
    }


    @DeleteMapping("/{loginId}")
    public String editDeleteMember(@PathVariable String loginId) {

        Optional<Member> member = memberRepository.findByLoginId(loginId);
        if (member.isEmpty()) {
            return "error/5xx";
        }
        memberRepository.delete(member);
        return "redirect:/admin/member";
    }

}
