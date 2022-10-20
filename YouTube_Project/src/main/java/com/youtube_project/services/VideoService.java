package com.youtube_project.services;

import com.youtube_project.model.dtos.video.VideoDTO;
import com.youtube_project.model.dtos.video.VideoUploadDTO;
import com.youtube_project.model.dtos.video.VideoWithNoOwnerDTO;
import com.youtube_project.model.entities.Video;
import com.youtube_project.model.exceptions.NotFoundException;
import com.youtube_project.model.relationships.videoreaction.VideoReaction;
import com.youtube_project.model.relationships.videoreaction.VideoReactionKey;
import com.youtube_project.model.relationships.videoreaction.VideoReactionRepository;
import com.youtube_project.model.entities.User;
import com.youtube_project.model.dtos.user.UserResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class VideoService extends AbstractService {

    public VideoDTO getById(long id){
        Video v = videoRepository.findById(id).orElseThrow(()->new NotFoundException("Video not found!"));
        VideoDTO dto = modelMapper.map(v,VideoDTO.class);
        dto.setOwner(modelMapper.map(v.getOwner(), UserResponseDTO.class));
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

    public List<VideoDTO> getByName(String name) {
        List<Video> videos = videoRepository.findAllByTitle(name);
        List<VideoDTO> videoDTOS = new ArrayList<>();
        for (Video video : videos) {
            VideoDTO dto = modelMapper.map(video,VideoDTO.class);
            dto.setOwner(modelMapper.map(video.getOwner(), UserResponseDTO.class));
            videoDTOS.add(dto);
        }
        return videoDTOS;
    }

    public boolean like(long videoId,long userId) {
        System.out.println(userId);
        User u = getUserById(userId);
        System.out.println(userId);
        VideoReaction videoReaction = new VideoReaction();
        VideoReactionKey videoReactionKey = new VideoReactionKey();
        videoReactionKey.setVideoId(videoId);
        videoReactionKey.setUserId(u.getId());

        videoReaction.setVideo(videoRepository.findById(videoId).get());
        videoReaction.setUser(u);
        videoReaction.setId(videoReactionKey);
        videoReaction.setReaction('l');
        if (videoReactionRepository.findById(videoReactionKey).isPresent()){
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
