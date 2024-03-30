package history.history.member.web.mypage;

import history.history.board.domain.board.Board;
import history.history.board.domain.service.BoardService;
import history.history.file.FileStore;
import history.history.member.domain.member.Member;
import history.history.member.domain.mypage.MyPageMember;
import history.history.member.domain.mypage.MyPageService;
import history.history.member.domain.repository.MemberRepository;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/my-page")
@RequiredArgsConstructor
@Slf4j
public class MyPageController {

    private final MemberRepository memberRepository;
    private final BoardService boardService;
    private final MyPageService myPageService;
    private final FileStore fileStore;
    private final VisitService visitService;


    @GetMapping("/{memberId}")
    public String myPageHome(
            @PathVariable String memberId,
            Model model,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember) {

        memberRepository.findByLoginId(loginMember.getLoginId())
                .ifPresent(loginId -> model.addAttribute("loginMember", loginId));

        memberRepository.findByMemberId(memberId)
                .ifPresent(member -> {
                    member.setName(member.getName());
                    member.setDescription(member.getDescription());
                    member.setProfile(member.getProfile());
                    log.info(member.getProfile());
                    log.info(member.getName());
                    member.setId(memberId);
                    model.addAttribute("member", member);
                });

        //해당 아이디 게시판
        List<Board> boards = boardService.userCheckService(memberId);
        Optional<Visitant> visit = visitService.addService();
        model.addAttribute("visit", visit);
        model.addAttribute("boards", boards);
        model.addAttribute("loginMember", loginMember);

        return "mypage/my-page";

    }

    @GetMapping("/edit")
    public String editGetMyPageHome(
            Model model,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {

        memberRepository.findByLoginId(member.getLoginId())
                .ifPresent(member1 -> model.addAttribute("member", member1));
        Optional<Visitant> visit = visitService.addService();
        model.addAttribute("visit", visit);

        model.addAttribute("loginMember", member);
        return "mypage/my-page-edit";
    }

    @PostMapping("/edit")
    public String editMyPageHome(
            @Validated
            @ModelAttribute("member") MyPageMember mpMember,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember) throws IOException {
        if (bindingResult.hasErrors()) {
            return "/mypage/my-page-edit";
        }
        Member member = new Member();
        String uploadImage = fileStore.storeFile(mpMember.getProfileImage());
        member.setProfile(uploadImage);
        if (uploadImage == null) {
            Optional<Member> fmember = memberRepository.findByLoginId(loginMember.getLoginId());
            member.setProfile(fmember.get().getProfile());
        }
        member.setName(mpMember.getName());
        member.setDescription(mpMember.getDescription());

        memberRepository.findByLoginId(loginMember.getLoginId())
                .ifPresent(member1 -> {
                    memberRepository.updateDescriptionMemberNameProfile(member1.getId(), member);
                    myPageService.boardNameUpdate(member1.getId(), mpMember);
                });
        redirectAttributes.addAttribute("memberId", loginMember.getId());
        return "redirect:/my-page/{memberId}";

    }


}
