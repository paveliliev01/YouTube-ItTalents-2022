package com.youtube_project.services;

import com.youtube_project.model.dtos.video.VideoDTO;
import com.youtube_project.model.dtos.video.VideoUploadDTO;
import com.youtube_project.model.dtos.video.VideoWithNoOwnerDTO;
import com.youtube_project.model.entities.Video;
import com.youtube_project.model.relationships.videoreactions.VideoReaction;
import com.youtube_project.model.relationships.videoreactions.VideoReactionKey;
import com.youtube_project.model.entities.User;
import com.youtube_project.model.dtos.user.UserResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class VideoService extends AbstractService {

    public VideoDTO getById(long id){
        System.out.println(id);
        Video v = getVideoById(id);
        VideoDTO dto = modelMapper.map(v,VideoDTO.class);
        //dto.setOwner(modelMapper.map(v.getOwner(), UserResponseDTO.class));
        return dto;
    }

    public VideoDTO upload(VideoUploadDTO dto, long userId) {
        User u = getUserById(userId);
        Video video = modelMapper.map(dto,Video.class);
        video.setDateOfUpload(LocalDate.now());
        video.setOwner(u);
        video.setVideoURL(video.getTitle().toLowerCase().trim().strip()+new Random().nextInt(1000));
        videoRepository.save(video);
        return modelMapper.map(video,VideoDTO.class);
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

        videoReaction.setVideo(videoRepository.findById(videoId).get());
        videoReaction.setUser(u);
        videoReaction.setId(videoReactionKey);
        videoReaction.setReaction(reaction);

        if (videoReactionRepository.findById(videoReactionKey).isPresent() && videoReactionRepository.findById(videoReactionKey).get().getReaction() == reaction){
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
}
