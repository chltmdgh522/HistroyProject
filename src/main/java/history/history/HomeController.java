package history.history;

import history.history.board.domain.board.Board;
import history.history.board.domain.repository.BoardRepository;
import history.history.board.domain.repository.BoardSearchCond;
import history.history.board.domain.service.BoardService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final VisitService visitService;
    private final BoardService boardService;

    @GetMapping("/")
    public String homeLoginV3Spring(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                    Model model,
                                    @ModelAttribute("boardSearchCond") BoardSearchCond boardSearchCond,
                                    @RequestParam(value = "page", defaultValue = "0") int page) {

        //사이트 방문자수
        Optional<Visitant> visit = visitService.addService();

        String memberName = boardSearchCond.getMemberName();
        String title = boardSearchCond.getTitle();

        Page<Board> paging = boardService.getList(memberName, title, page);

        List<Board> boards = boardRepository.findSearchAll(boardSearchCond);
        List<Member> pointMember = memberRepository.findTotalGivePoint();


        //세션이 유지되면 로그인으로 이동
        model.addAttribute("loginMember", loginMember);
        model.addAttribute("paging", paging);
        model.addAttribute("board", boards);
        model.addAttribute("visit", visit);
        model.addAttribute("pointMember", pointMember);
        return "loginHome";
    }

    @GetMapping("/donation")
    public String homeDonation(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                Model model) {

        //사이트 방문자수
        Optional<Visitant> visit = visitService.addService();


        List<Member> pointMember = memberRepository.findTotalGivePoint();

        List<Board> boards = boardRepository.findAll();
        List<Board> all = new ArrayList<>();
        for (Board board : boards) {
            if (board.isBoardType()) {
                all.add(board);
            }
        }
        model.addAttribute("loginMember", loginMember);


        //세션이 유지되면 로그인으로 이동
        model.addAttribute("board", all);
        model.addAttribute("visit", visit);
        model.addAttribute("pointMember", pointMember);
        return "donationHome";
    }

    @GetMapping("/free")
    public String homeFree(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                           Model model,
                           @ModelAttribute("boardSearchCond") BoardSearchCond boardSearchCond,
                           @RequestParam(value = "page", defaultValue = "0") int page) {

        //사이트 방문자수
        Optional<Visitant> visit = visitService.addService();

        String memberName = boardSearchCond.getMemberName();
        String title = boardSearchCond.getTitle();

        Page<Board> paging = boardService.getList(memberName, title, page);


        List<Board> boards = boardRepository.findSearchAll(boardSearchCond);
        List<Member> pointMember = memberRepository.findTotalGivePoint();

        model.addAttribute("loginMember", loginMember);
        //세션이 유지되면 로그인으로 이동
        model.addAttribute("paging", paging);
        model.addAttribute("board", boards);
        model.addAttribute("visit", visit);
        model.addAttribute("pointMember", pointMember);
        return "freeHome";
    }


}