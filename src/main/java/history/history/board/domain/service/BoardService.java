package history.history.board.domain.service;


import history.history.board.domain.board.Board;
import history.history.board.domain.board.BoardDto;
import history.history.board.domain.repository.BoardRepository;
import history.history.board.domain.repository.jpa.JpaBoardRepository;
import history.history.comment.domain.repository.jdbctemplate.JdbcTemplateCommentRepository;
import history.history.file.FileStore;
import history.history.member.domain.member.Member;
import history.history.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final JdbcTemplateCommentRepository commentRepository;
    private final JpaBoardRepository jpaBoardRepository;
    private final FileStore fileStore;

    //마이페이지에서 해당유저 게시물 보여주기
    public List<Board> userCheckService(String memberId) {
        List<Board> all = boardRepository.findAll();
        List<Board> boards = new ArrayList<>();
        for (Board board : all) {
            if (board.getMemberId().equals(memberId)) {
                boards.add(board);
            }
        }
        return boards;
    }

    public Member findByMemberId(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElse(null);
    }

    // 유저의 계정 게시물마다 boardId증가
    public int addIdService(String memberId) {
        List<Board> all = boardRepository.findAll();
        int max = 1;
        if (all != null) {
            for (Board fboard : all) {
                if (fboard.getMemberId().equals(memberId)) {
                    max = Math.max(max, Integer.parseInt(fboard.getBoardId()));
                    max += 1;
                }
            }
        }
        return max;
    }

    public Board boardSaveService(Member member, BoardDto fboard) throws IOException {
        Board board = new Board();
        board.setMemberName(member.getName());
        board.setMemberId(member.getId());
        int boardId = addIdService(member.getId());
        board.setBoardId(String.valueOf(boardId));
        board.setTitle(fboard.getTitle());
        board.setContent(fboard.getContent());
        board.setBoardType(fboard.isBoardType());

        if (board.isBoardType()) {
            board.setOptionPoint(fboard.getOptionPoint());
        }


        //게시물 생성시간
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일  HH시 mm분");
        Date date = new Date();
        board.setDate(sdf.format(date));


        //게시물 사진 저장
        String uploadImage = fileStore.storeFile(fboard.getBoardImage());
        if (uploadImage == null) {
            log.info("d123");
            board.setBoardImage("boardBasic.jpg");
        } else {
            board.setBoardImage(uploadImage);
        }
        boardRepository.save(board);

        //게시물 생성할때 포인트 점수 100
        memberRepository.updatePoint(member.getId(), member.getPoint() + 100);
        return board;
    }

    public void boardUpdateService(Member member, BoardDto fboard) throws IOException {
        Board board = new Board();
        board.setMemberName(member.getName());
        board.setMemberId(member.getId());
        int boardId = addIdService(member.getId());
        board.setBoardId(String.valueOf(boardId));
        board.setTitle(fboard.getTitle());
        board.setContent(fboard.getContent());
        board.setBoardType(fboard.isBoardType());

        //게시물 생성시간
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일  HH시 mm분");
        Date date = new Date();
        board.setDate(sdf.format(date));

        log.info("abc={}", board.getDate());


        //게시물 사진 저장
        log.info("aa={}", fboard.getBoardImage());
        String uploadImage = fileStore.storeFile(fboard.getBoardImage());
        board.setBoardImage(uploadImage);
        boardRepository.save(board);

        //게시물 생성할때 포인트 점수 100
        memberRepository.updatePoint(member.getId(), member.getPoint() + 100);
    }

    //홈에서 게시판 클릭하면 게시판 보여주기 및 편집 버튼
    public Board boardCheckService(String memberId, String boardId) {


        return boardRepository.findByMemberIdAndBoardId(memberId, boardId)
                .orElse(null);
    }

    //게시물 조회수 증가
    public void addViewCount(String memberId, String boardId) {
        Optional<Board> board = boardRepository.findByMemberIdAndBoardId(memberId, boardId);
        int count = board.get().getViewCount() + 1;
        boardRepository.updateViewCount(board.get().getId(), count);
    }


    //페이징
    public Page<Board> getList(String memberName, String title, int page) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("date"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        log.info("a={}", memberName);
        log.info("b={}", title);

        if (memberName.equals("") && title.equals("")) {
            return jpaBoardRepository.findAllByBoardTypeFalse(pageable);
        }

        return jpaBoardRepository.findByMemberNameContainingAndTitleContainingAndBoardTypeFalse(memberName, title, pageable);
    }

    public void delete(Board board) {
        commentRepository.deleteBoard(board.getBoardId());
        boardRepository.delete(board);

    }
}
