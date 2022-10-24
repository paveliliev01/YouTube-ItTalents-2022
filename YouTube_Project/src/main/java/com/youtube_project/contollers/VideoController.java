package com.youtube_project.contollers;

import com.youtube_project.model.dtos.video.VideoResponseDTO;
import com.youtube_project.model.dtos.video.VideoSimpleResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/videos")
public class VideoController extends MasterController {


    @PostMapping("/users/{uid}/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadVideo(@PathVariable(value = "uid") long uid,
                              @RequestParam(value = "file") MultipartFile video,
                              @RequestParam(value = "title") String title,
                              @RequestParam(value = "description") String description,
                              @RequestParam(value = "Private") Boolean isPrivate,
                              HttpServletRequest request) {
        sessionManager.validateLogin(request);
        sessionManager.checkIfAuthorized(uid, request);
        return videoService.upload(uid, video, title, description, isPrivate);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.FOUND)
    public VideoResponseDTO getVideoById(@PathVariable long id) {
        return videoService.getById(id);
    }

    @GetMapping("search/{title}")
    @ResponseStatus(HttpStatus.FOUND)
    public List<VideoResponseDTO> getByTitle(@PathVariable String title) {
        return videoService.getByTitle(title);
    }

    @PostMapping("/{vid}/like")
    public String likeVideo(@PathVariable long vid, HttpServletRequest request) {
        sessionManager.validateLogin(request);
        long loggedUserId = sessionManager.getSessionUserId(request);
        return videoService.reactToVideo(vid, loggedUserId, LIKE);
    }

    @PostMapping("/{vid}/dislike")
    public String dislikeVideo(@PathVariable long vid, HttpServletRequest request) {
        sessionManager.validateLogin(request);
        long loggedUserId = sessionManager.getSessionUserId(request);
        return videoService.reactToVideo(vid, loggedUserId, DISLIKE);
    }

    @GetMapping("/likedVideos")
    public List<VideoResponseDTO> getAllLikedVideos(HttpServletRequest request) {
        sessionManager.validateLogin(request);
        return videoService.getAllVideosWithReaction(sessionManager.getSessionUserId(request), LIKE);
    }

    @GetMapping("/dislikedVideos")
    public List<VideoResponseDTO> getAllDislikedVideos(HttpServletRequest request) {
        sessionManager.validateLogin(request);
        return videoService.getAllVideosWithReaction(sessionManager.getSessionUserId(request), DISLIKE);
    }

    @PostMapping("/{vid}/watch")
    public VideoResponseDTO watchVideo(@PathVariable long vid, HttpServletRequest request) {
        sessionManager.validateLogin(request);
        long loggedUserId = sessionManager.getSessionUserId(request);
        return videoService.watch(vid, loggedUserId);
    }

    @PutMapping("/{vid}/delete")
    public String deleteVideo(@PathVariable long vid, HttpServletRequest request) {
        sessionManager.validateLogin(request);
        long loggedUserId = sessionManager.getSessionUserId(request);
        return videoService.delete(vid, loggedUserId);
    }

    @GetMapping("/mostWatched")
    public List<VideoSimpleResponseDTO> getVideosByMostWatched(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int rowNumbers) {
        return videoService.getMostWatched(rowNumbers, pageNumber);
    }

    @GetMapping("/mostLiked")
    public List<VideoSimpleResponseDTO> getVideosByMostLiked(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int rowNumbers) {
        return videoService.getMostLiked(rowNumbers, pageNumber);
    }
}
