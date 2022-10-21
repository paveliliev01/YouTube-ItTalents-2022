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


    @PostMapping("/{vid}/add")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO addComment(@RequestBody CommentAddDTO comment, @PathVariable long vid, HttpServletRequest request) {
        sessionManager.validateLogin(request);
        return commentService.addComment(comment,vid,sessionManager.getSessionUserId(request));
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteCommentById(@RequestParam long vid,@RequestParam int cid, HttpServletRequest request){
        sessionManager.validateLogin(request);
        commentService.deleteById(vid,cid,sessionManager.getSessionUserId(request));
        return ResponseEntity.status(204).body("Comment deleted successfully!");
    }

    @GetMapping("/{cid}")
    public CommentDTO getCommentById(@PathVariable long cid) {
        return commentService.getById(cid);
    }
    @GetMapping("/{vid}/all")
    public List<CommentResponseDTO> getAllCommentsOfVideo(@PathVariable long vid){
        return commentService.getAllCommentsOfVideo(vid);
    }

    @PostMapping("/like/{cid}")
    @ResponseStatus(HttpStatus.OK)
    public boolean likeComment(@PathVariable long cid,HttpServletRequest request){
        sessionManager.validateLogin(request);
        return commentService.reactToComment(cid,sessionManager.getSessionUserId(request),'l');
    }

    @PostMapping("dislike/{cid}")
    @ResponseStatus(HttpStatus.OK)
    public boolean dislikeComment(@PathVariable long cid,HttpServletRequest request){
        sessionManager.validateLogin(request);
        return commentService.reactToComment(cid,sessionManager.getSessionUserId(request),'d');
    }

    @PostMapping("/reply/{cid}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO replyToAComment(@RequestBody CommentAddDTO commentDTO,@PathVariable long cid,HttpServletRequest request){
        sessionManager.validateLogin(request);
        return commentService.replyToAComment(commentDTO,cid,sessionManager.getSessionUserId(request));
    }
}
