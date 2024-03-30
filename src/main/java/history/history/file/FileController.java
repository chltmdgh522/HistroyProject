package history.history.file;

import history.history.board.domain.board.Board;
import history.history.board.domain.repository.BoardRepository;
import history.history.member.domain.member.Member;
import history.history.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.util.Optional;

@Controller
@RequestMapping("/my-page")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileStore fileStore;
    private final MemberRepository memberRepository;

    private final BoardRepository boardRepository;

    //마이페이지 프로필
    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource profileImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    //댓글 프로필
    @ResponseBody
    @GetMapping("/imagesV2/{memberId}")
    public Resource profileImageV2(@PathVariable String memberId) throws MalformedURLException {
        Optional<Member> member = memberRepository.findByMemberId(memberId);
        return new UrlResource("file:" + fileStore.getFullPath(member.get().getProfile()));
    }


    //게시글 사진
    @ResponseBody
    @GetMapping("/imagesV3/{boardId}")
    public Resource profileImageV3(@PathVariable Long boardId) throws MalformedURLException {
        Optional<Board> board = boardRepository.findById(boardId);
        return new UrlResource("file:" + fileStore.getFullPath(board.get().getBoardImage()));
    }

    //홈 사진
    @ResponseBody
    @GetMapping("/imageV4/{image}")
    public Resource profileImageV4(@PathVariable String image) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(image));
    }
}
