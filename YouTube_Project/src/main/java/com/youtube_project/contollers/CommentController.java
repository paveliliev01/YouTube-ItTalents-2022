package com.youtube_project.contollers;
import com.youtube_project.model.dtos.comment.CommentAddDTO;
import com.youtube_project.model.dtos.comment.CommentDTO;
import com.youtube_project.model.dtos.comment.CommentResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController extends MasterController {


    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public String deleteCommentById(@RequestParam long vid,@RequestParam int cid, HttpServletRequest request){
        sessionManager.validateLogin(request);
        return commentService.deleteById(vid,cid,sessionManager.getSessionUserId(request));
    }

    @GetMapping("/{cid}")
    public CommentDTO getCommentById(@PathVariable long cid) {
        return commentService.getById(cid);
    }
    @GetMapping("all/video/{vid}")
    public List<CommentResponseDTO> getAllCommentsOfVideo(@PathVariable long vid){
        return commentService.getAllCommentsOfVideo(vid);
    }

    @PostMapping("{cid}/like")
    @ResponseStatus(HttpStatus.OK)
    public String likeComment(@PathVariable long cid,HttpServletRequest request){
        sessionManager.validateLogin(request);
        return commentService.reactToComment(cid,sessionManager.getSessionUserId(request),LIKE);
    }

    @PostMapping("{cid}/dislike")
    @ResponseStatus(HttpStatus.OK)
    public String dislikeComment(@PathVariable long cid,HttpServletRequest request){
        sessionManager.validateLogin(request);
        return commentService.reactToComment(cid,sessionManager.getSessionUserId(request),DISLIKE);
    }

    @PostMapping("{cid}/reply")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO replyToAComment(@RequestBody CommentAddDTO commentDTO,@PathVariable long cid,HttpServletRequest request){
        sessionManager.validateLogin(request);
        return commentService.replyToAComment(commentDTO,cid,sessionManager.getSessionUserId(request));
    }
}
