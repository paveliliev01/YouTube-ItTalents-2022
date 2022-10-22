package com.youtube_project.services;

import com.youtube_project.model.dtos.video.VideoDTO;
import com.youtube_project.model.dtos.video.VideoWithNoOwnerDTO;
import com.youtube_project.model.entities.Video;
import com.youtube_project.model.exceptions.BadRequestException;
import com.youtube_project.model.relationships.videoreactions.VideoReaction;
import com.youtube_project.model.relationships.videoreactions.VideoReactionKey;
import com.youtube_project.model.entities.User;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService extends AbstractService {

    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 200;

    public VideoDTO getById(long id){
        System.out.println(id);
        Video v = getVideoById(id);
        VideoDTO dto = modelMapper.map(v,VideoDTO.class);
        //dto.setOwner(modelMapper.map(v.getOwner(), UserResponseDTO.class));
        return dto;
    }

    private void validateVideoInfo(MultipartFile file,String title,String description){
        if(file.isEmpty()){
            throw new BadRequestException("The file is not attached!");
        }
        if(title.isEmpty() || title.length() > MAX_TITLE_LENGTH){
            throw new BadRequestException("Incorrect video title");
        }
        if (description.isEmpty() || description.length() > MAX_DESCRIPTION_LENGTH){
            throw new BadRequestException("Incorrect description!");
        }
        if (!file.getContentType().equals("video/mp4")) {
            throw new BadRequestException("Invalid video format!");
        }
    }
    public String upload(long uid,MultipartFile file,String title,String description,boolean isPrivate) {
        try {
            title = title.trim();
            description = description.trim();
            validateVideoInfo(file,title,description);

            User user = getUserById(uid);
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            String videoURL = "uploads" + File.separator + "videos" +File.separator+ System.nanoTime() + "_" + uid + "." + extension;
            File f = new File(videoURL);
            if(!f.exists()){
                Files.copy(file.getInputStream(),f.toPath());
            }else {
                //this should never happen!
                throw new BadRequestException("The file already exists!");
            }

            Video video = new Video();
            video.setTitle(title);
            video.setDescription(description);
            video.setOwner(user);
            video.setDateOfUpload(LocalDate.now());
            video.setVideoURL(videoURL);
            video.setPrivate(isPrivate);

            videoRepository.save(video);

            return "Successfully uploaded " + file.getName();
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage(),e);
        }
    }

    public List<VideoDTO> getByTitle(String title) {
        List<Video> videos = videoRepository.findAllByTitle(title);
        List<VideoDTO> videoDTOS;
        videoDTOS = videos.stream().map(video -> modelMapper.map(video,VideoDTO.class)).collect(Collectors.toList());
        return videoDTOS;
    }

    public boolean reactToVideo(long videoId, long userId, char reaction) {
        User u = getUserById(userId);
        VideoReaction videoReaction = new VideoReaction();
        VideoReactionKey videoReactionKey = new VideoReactionKey();
        videoReactionKey.setVideoId(videoId);
        videoReactionKey.setUserId(u.getId());

        videoReaction.setId(videoReactionKey);
        videoReaction.setVideo(getVideoById(videoId));
        videoReaction.setUser(u);
        videoReaction.setReaction(reaction);

        if (videoReactionRepository.findById(videoReactionKey).isPresent() &&
                videoReactionRepository.findById(videoReactionKey).get().getReaction() == reaction){
            videoReactionRepository.delete(videoReaction);
            return false;
        }
        videoReactionRepository.save(videoReaction);
        return true;
    }

    public List<VideoWithNoOwnerDTO> getAllVideosWithReaction(long id, char c) {
        User u = getUserById(id);
        List<VideoReaction> videoReactions = videoReactionRepository.findAllByUserAndReaction(u, c);
        List<VideoWithNoOwnerDTO> postsLikedList = new ArrayList<>();
        for (VideoReaction videoReaction : videoReactions) {
            VideoWithNoOwnerDTO postLiked = modelMapper.map(videoReaction.getVideo(), VideoWithNoOwnerDTO.class);
            postsLikedList.add(postLiked);
        }
        return postsLikedList;
    }

    public int watch(long vid, long loggedUserId) {
        Video video = getVideoById(vid);
        User user = getUserById(loggedUserId);
        if(!user.getWatchedVideos().contains(video)){
            user.getWatchedVideos().add(video);
            userRepository.save(user);
        }
        return video.getViewers().size();
    }
}
