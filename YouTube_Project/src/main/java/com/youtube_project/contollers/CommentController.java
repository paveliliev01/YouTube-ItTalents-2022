package com.youtube_project.contollers;
import com.youtube_project.models.comment.CommentAddDTO;
import com.youtube_project.models.comment.CommentDTO;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/comments")
public class CommentController extends MasterController {

    @PostMapping("/{vid}/add")
    public CommentDTO addComment(@RequestBody CommentAddDTO comment, @PathVariable long vid, HttpServletRequest request) {
        return commentService.addComment(comment,vid,sessionManager.getSessionUserId(request));
    }

    @GetMapping("/{id}")
    public CommentDTO getCommentById(@PathVariable long id) {
        return commentService.getCommentById(id);
    }
}
