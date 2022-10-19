package com.youtube_project.contollers;

import com.youtube_project.models.exceptions.UnauthorizedException;
import com.youtube_project.models.video.VideoDTO;
import com.youtube_project.models.video.VideoUploadDTO;
import com.youtube_project.models.video.VideoWithNoOwnerDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/videos")
public class VideoController extends MasterController{


    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public VideoDTO uploadVideo(@RequestBody VideoUploadDTO video, HttpServletRequest request){
        sessionManager.validateLogin(request);
        return videoService.upload(video,sessionManager.getSessionUserId(request));
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.FOUND)
    public VideoDTO getVideoById(@PathVariable long id){
        return videoService.getById(id);
    }

    @GetMapping("search/{name}")
    @ResponseStatus(HttpStatus.FOUND)
    public List<VideoDTO> getByName(@PathVariable String name){
        return videoService.getByName(name);
    }

    @PostMapping("/{vid}/like")
    public boolean likeVideo(@PathVariable long vid,HttpServletRequest request){
        sessionManager.validateLogin(request);
        return videoService.like(vid,sessionManager.getSessionUserId(request));
    }

    @GetMapping("/likedVideo")
    public List<VideoWithNoOwnerDTO> getAllLikedVideos(HttpServletRequest request) {
        sessionManager.validateLogin(request);
        return videoService.getAllVideosWithReaction(sessionManager.getSessionUserId(request), 'l');
    }

    @GetMapping("/dislikedVideos")
    public List<VideoWithNoOwnerDTO> getAllDislikedVideos(HttpServletRequest request) {
        sessionManager.validateLogin(request);
        return videoService.getAllVideosWithReaction(sessionManager.getSessionUserId(request), 'd');
    }

}
