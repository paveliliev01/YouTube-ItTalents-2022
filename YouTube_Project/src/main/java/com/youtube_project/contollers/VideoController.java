package com.youtube_project.contollers;

import com.youtube_project.model.dtos.video.VideoResponseDTO;
import com.youtube_project.model.dtos.video.VideoSimpleResponseDTO;
import com.youtube_project.model.entities.Video;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/videos")
public class VideoController extends MasterController {


    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadVideo(@RequestParam(value = "file") MultipartFile video,
                              @RequestParam(value = "title") String title,
                              @RequestParam(value = "description") String description,
                              @RequestParam(value = "Private") Boolean isPrivate,
                              HttpServletRequest request) {
        sessionManager.validateLogin(request);
        long loggedUserId = sessionManager.getSessionUserId(request);
        return videoService.upload(loggedUserId, video, title, description, isPrivate);
    }

    @GetMapping("/{vid}")
    @ResponseStatus(HttpStatus.FOUND)
    public VideoResponseDTO getVideoById(@PathVariable long vid) {
        return videoService.getById(vid);
    }

    @GetMapping("/searchBy")
    @ResponseStatus(HttpStatus.FOUND)
    public List<VideoResponseDTO> getByTitle(@RequestParam(value = "title") String title,
                                  @RequestParam(defaultValue = "0") int pageNumber,
                                  @RequestParam(defaultValue = "1") int rowNumbers)
    {
        return videoService.getByTitle(title,pageNumber,rowNumbers);
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
        long loggedUserId = sessionManager.getSessionUserId(request);
        return videoService.getAllVideosWithReaction(loggedUserId, LIKE);
    }

    @GetMapping("/dislikedVideos")
    public List<VideoResponseDTO> getAllDislikedVideos(HttpServletRequest request) {
        sessionManager.validateLogin(request);
        long loggedUserId = sessionManager.getSessionUserId(request);
        return videoService.getAllVideosWithReaction(loggedUserId, DISLIKE);
    }

    @PostMapping("/{vid}/watch")
    public VideoResponseDTO watchVideo(@PathVariable long vid, HttpServletRequest request) {
        sessionManager.validateLogin(request);
        long loggedUserId = sessionManager.getSessionUserId(request);
        return videoService.watch(vid, loggedUserId);
    }

    @DeleteMapping("/{vid}/delete")
    public String deleteVideo(@PathVariable long vid, HttpServletRequest request) {
        sessionManager.validateLogin(request);
        long loggedUserId = sessionManager.getSessionUserId(request);
        return videoService.delete(vid, loggedUserId);
    }

    @GetMapping("/{uid}/uploads")
    public List<VideoResponseDTO> getUserUploads(@PathVariable long uid, HttpServletRequest request) {
        sessionManager.validateLogin(request);
        return videoService.getUploads(uid);
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

    @PutMapping("/clear_viewed_history")
    public String deleteViewHistory(HttpServletRequest request) {
        sessionManager.validateLogin(request);
        long loggedUserId = sessionManager.getSessionUserId(request);
        videoService.deleteViewHistory(loggedUserId);
        return null;
    }
}
