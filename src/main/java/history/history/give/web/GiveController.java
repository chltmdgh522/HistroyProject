package history.history.give.web;

import history.history.board.domain.board.Board;
import history.history.board.domain.repository.BoardRepository;
import history.history.give.domain.give.Give;
import history.history.give.domain.reposiotry.GiveRepository;
import history.history.member.domain.member.Member;
import history.history.member.domain.repository.MemberRepository;
import history.history.member.web.session.SessionConst;
import history.history.visitant.domain.service.VisitService;
import history.history.visitant.domain.visit.Visitant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/give/{boardId}")
@RequiredArgsConstructor
public class GiveController {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final GiveRepository giveRepository;

    private final VisitService visitService;

    @GetMapping
    public String giveGet(
            @ModelAttribute Give give,
            @PathVariable String boardId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            Model model) {
        Optional<Member> member = memberRepository.findByMemberId(loginMember.getId());
        model.addAttribute("point", member.get().getPoint());

        Optional<Visitant> visit = visitService.addService();
        model.addAttribute("visit", visit);
        model.addAttribute("give", give);
        model.addAttribute("loginMember", loginMember);
        return "give/give";
    }

    @PostMapping
    public String givePost(@ModelAttribute Give give,
                           @PathVariable Long boardId,
                           @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                           RedirectAttributes redirectAttributes) {
        Optional<Member> member = memberRepository.findByMemberId(loginMember.getId());

        give.setBoardId(boardId);
        give.setMemberId(loginMember.getId());
        give.setName(member.get().getName());
        give.setEmail(member.get().getEmail());
        giveRepository.save(give);

        //기존 포인트 차감
        int point = member.get().getPoint();
        point -= give.getGivePoint();
        memberRepository.updatePoint(loginMember.getId(), point);

        //총 기부 포인트
        int totalGivePoint = member.get().getTotalGivePoint();
        totalGivePoint += give.getGivePoint();
        memberRepository.updateTotalGivePoint(loginMember.getId(), totalGivePoint);

        //기존 게시판 이동
        Optional<Board> board = boardRepository.findById(boardId);
        String memberId = board.get().getMemberId();
        redirectAttributes.addAttribute("memberId", memberId);

        //기존 게시판 포인트 증가
        int resultPoint = board.get().getGivePoint() + give.getGivePoint();
        boardRepository.updateBoardPoint(boardId, resultPoint);

        String fboardId = board.get().getBoardId();
        redirectAttributes.addAttribute("fboardId", fboardId);

        return "redirect:/board/{memberId}/{fboardId}";

    }

}
