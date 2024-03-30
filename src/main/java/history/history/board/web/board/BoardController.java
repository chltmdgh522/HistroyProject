package history.history.board.web.board;

import history.history.board.domain.board.Board;
import history.history.board.domain.board.BoardDto;
import history.history.board.domain.repository.BoardRepository;
import history.history.board.domain.service.BoardService;
import history.history.comment.domain.Comment;
import history.history.comment.domain.repository.jdbctemplate.JdbcTemplateCommentRepository;
import history.history.file.FileStore;
import history.history.give.domain.give.Give;
import history.history.give.domain.reposiotry.GiveRepository;
import history.history.member.domain.member.Member;
import history.history.member.web.session.SessionConst;
import history.history.visitant.domain.service.VisitService;
import history.history.visitant.domain.visit.Visitant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    int num = 0;
    private final VisitService visitService;
    private final BoardRepository boardRepository; // 굳이 서비스 만들기 귀찮다...

    private final JdbcTemplateCommentRepository commentRepository;
    private final BoardService boardService;
    private final GiveRepository giveRepository;

    private final FileStore fileStore;

    //게시판 생성뷰
    @GetMapping
    public String boardCreate(
            @ModelAttribute Board board,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            Model model) {
        Member member = boardService.findByMemberId(loginMember.getId());
        if (member == null) {
            return "/error/4xx";
        }
        Optional<Visitant> visit = visitService.addService();
        board.setMemberName(member.getName());
        board.setMemberId(member.getId());
        model.addAttribute("board", board);
        model.addAttribute("loginMember", loginMember);

        model.addAttribute("visit", visit);
        return "board/boardCreate";
    }

    //게시판 생성 처리
    @PostMapping
    public String createBoard(
            @ModelAttribute("board") BoardDto board,
            RedirectAttributes redirectAttributes,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember
    ) throws IOException {
        Member member = boardService.findByMemberId(loginMember.getId());
        if (member == null) {
            return "/error/4xx";
        }
        Board board1 = boardService.boardSaveService(member, board);

        redirectAttributes.addAttribute("memberId", loginMember.getId());
        redirectAttributes.addAttribute("boardId", board1.getBoardId());

        return "redirect:/board/{memberId}/{boardId}";
    }

    //유저 게시판 보기
    @GetMapping("/{memberId}/{boardId}")
    public String checkBoard(@PathVariable String memberId,
                             @PathVariable String boardId,
                             @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                             @ModelAttribute("comment") Comment comment,
                             Model model) {
        //게시물 조회수 증가
        boardService.addViewCount(memberId, boardId);

        Board board = boardService.boardCheckService(memberId, boardId);
        if (board == null) {
            return "error/4xx";
        }
        if (board.getModify().equals("M")) {
            model.addAttribute("update", "(수정됨)");
        }
        if (board.getModify().equals("X")) {
            model.addAttribute("update", "");
        }
        int process = (int) ((float) board.getGivePoint() / board.getOptionPoint() * 100);

        log.info("d={}", process);
        model.addAttribute("loginMember", loginMember);

        Optional<Board> fboard = boardRepository.findByMemberIdAndBoardId(memberId, boardId);

        List<Comment> fcomment = commentRepository.findByBoardId(fboard.get().getId());


        List<Give> giveComment = giveRepository.findByBoardId(fboard.get().getId());
        Optional<Visitant> visit = visitService.addService();
        model.addAttribute("visit", visit);
        model.addAttribute("process", process);
        model.addAttribute("memberId", memberId);
        model.addAttribute("fcomment", fcomment);
        model.addAttribute("board", board);
        model.addAttribute("give", giveComment);
        return "board/board";
    }

    //게시판 삭제
    @DeleteMapping("/{memberId}/{boardId}")
    public String editDeleteBoard(@PathVariable String memberId,
                                  @PathVariable String boardId) {

        Board board = boardService.boardCheckService(memberId, boardId);
        if (board == null) {
            return "error/4xx";
        }
        boardService.delete(board);
        return "redirect:/";
    }

    //게시판 편집뷰
    @GetMapping("/{boardId}/edit")
    public String editBoard(
            @PathVariable String boardId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            Model model) {
        Board board = boardService.boardCheckService(loginMember.getId(), boardId);
        if (board == null || !Objects.equals(board.getMemberId(), loginMember.getId())) {
            return "error/4xx";
        }
        Optional<Visitant> visit = visitService.addService();
        model.addAttribute("visit", visit);
        model.addAttribute("board", board);
        model.addAttribute("loginMember", loginMember);
        return "board/boardEdit";
    }

    //게시판 편집 처리
    @PostMapping("/{boardId}/edit")
    public String editPostBoard(@PathVariable String boardId,
                                @ModelAttribute BoardDto fboard,
                                RedirectAttributes redirectAttributes,
                                @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember) throws IOException {
        Board board = boardService.boardCheckService(loginMember.getId(), boardId);
        if (board == null) {
            return "error/4xx";
        }

        board.setTitle(fboard.getTitle());
        board.setContent(fboard.getContent());

        String uploadImage = fileStore.storeFile(fboard.getBoardImage());
        board.setBoardImage(uploadImage);

        if (uploadImage == null) {
            Optional<Board> ffboard = boardRepository.findById(board.getId());
            board.setBoardImage(ffboard.get().getBoardImage());
        }

        if (board.getModify().equals("X")) {
            board.setModify("M");
        }
        boardRepository.updateTitleAndContent(board);

        redirectAttributes.addAttribute("memberId", loginMember.getId());

        return "redirect:/board/{memberId}/{boardId}";
    }
}
