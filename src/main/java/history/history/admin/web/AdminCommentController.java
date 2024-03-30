package history.history.admin.web;

import history.history.admin.domain.comment.CommentSearch;
import history.history.admin.domain.comment.JpaComment;
import history.history.admin.domain.service.AdminService;
import history.history.comment.domain.repository.jdbctemplate.CommentRepository;
import history.history.member.domain.member.Member;
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
public class AdminCommentController {
    private final AdminService adminService;

    private final VisitService visitService;


    private final CommentRepository commentRepository;

    @GetMapping("/comment")
    public String memberInformation(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                    @ModelAttribute("commentSearchCond") CommentSearch cond,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    Model model) {
        if (loginMember.getRole().equals("X")) {
            return "redirect:/";
        }
        Page<JpaComment> list = adminService.getListComment(cond.getMemberName(), cond.getContent(), page);

        Optional<Visitant> visit = visitService.addService();
        model.addAttribute("visit", visit);
        model.addAttribute("paging", list);
        model.addAttribute("loginMember",loginMember);
        return "admin/adminComment";
    }

    @DeleteMapping("/comment/{commentId}")
    public String editDeleteComment(@PathVariable Long commentId) {

        commentRepository.delete(commentId);

        return "redirect:/admin/comment";
    }

}
