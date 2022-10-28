package com.youtube_project.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.youtube_project.model.dtos.video.VideoDAO;
import com.youtube_project.model.dtos.video.VideoResponseDTO;
import com.youtube_project.model.dtos.video.VideoSimpleResponseDTO;
import com.youtube_project.model.entities.User;
import com.youtube_project.model.entities.Video;
import com.youtube_project.model.exceptions.BadRequestException;
import com.youtube_project.model.exceptions.NotFoundException;
import com.youtube_project.model.exceptions.UnauthorizedException;
import com.youtube_project.model.relationships.videoreactions.VideoReaction;
import com.youtube_project.model.relationships.videoreactions.VideoReactionKey;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class VideoService extends AbstractService {

    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 200;

    @Autowired
    private VideoDAO videoDAO;
    @Autowired
    private AmazonS3 s3Client;
    @Value("${application.bucket.name}" + "/videos")
    private String bucketName;


    public VideoResponseDTO getById(long id) {
        Video v = getVideoById(id);
        VideoResponseDTO dto = videoToResponseVideoDTO(v);
        return dto;
    }


    private void validateVideoInfo(MultipartFile file, String title, String description) {
        if (file.isEmpty()) {
            throw new BadRequestException("The file is not attached!");
        }
        if (title.isEmpty() || title.length() > MAX_TITLE_LENGTH) {
            throw new BadRequestException("Incorrect video title");
        }
        if (description.isEmpty() || description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new BadRequestException("Incorrect description!");
        }
        if (!file.getContentType().equals("video/mp4")) {
            throw new BadRequestException("Invalid video format!");
        }
    }

    private File convertMultipartFileToFIle(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            System.out.println("CIRK");
        }

        return convertedFile;
    }

    public String upload(long loggedUserId, MultipartFile file, String title, String description, boolean isPrivate) {

        User user = getUserById(loggedUserId);
        if (!user.isVerified()) {
            throw new UnauthorizedException("Please verify your account first!");
        }

        title = title.trim();
        description = description.trim();
        validateVideoInfo(file, title, description);


        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String videoURL = "uploads" + File.separator + "videos" + File.separator + System.nanoTime() + "_" + loggedUserId + "." + extension;

        File file1 = convertMultipartFileToFIle(file);
        s3Client.putObject(new PutObjectRequest(bucketName, videoURL, file1));
        file1.delete();

        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setOwner(user);
        video.setDateOfUpload(LocalDate.now());
        video.setVideoURL(videoURL);
        video.setPrivate(isPrivate);

        videoRepository.save(video);

        return "Successfully uploaded " + file.getName();
    }

    public List<VideoResponseDTO> getByTitle(String title, int pageNumber, int rowNumbers) {
        Pageable page = PageRequest.of(pageNumber, rowNumbers);
        return videoRepository.findAllByTitle(title,page).stream().map(this::videoToResponseVideoDTO).collect(Collectors.toList());
    }

    public String reactToVideo(long videoId, long userId, char reaction) {
        User u = getUserById(userId);
        if (!u.isVerified()) {
            throw new UnauthorizedException("Please verify your account first!");
        }

        VideoReaction videoReaction = new VideoReaction();
        VideoReactionKey videoReactionKey = new VideoReactionKey();
        videoReactionKey.setVideoId(videoId);
        videoReactionKey.setUserId(u.getId());

        videoReaction.setId(videoReactionKey);
        videoReaction.setVideo(getVideoById(videoId));
        videoReaction.setUser(u);
        videoReaction.setReaction(reaction);

        if (videoReactionRepository.findById(videoReactionKey).isPresent() &&
                videoReactionRepository.findById(videoReactionKey).get().getReaction() == reaction) {
            videoReactionRepository.delete(videoReaction);
            return "Successfully removed reaction " + videoReactionRepository.findAllByVideoAndReaction(getVideoById(videoId),reaction).size();
        }
        videoReactionRepository.save(videoReaction);
        return "Successfully reacted to video " + videoReactionRepository.findAllByVideoAndReaction(getVideoById(videoId),reaction).size();
    }

    public List<VideoResponseDTO> getAllVideosWithReaction(long id, char c) {
        User u = getUserById(id);
        List<VideoReaction> videoReactions = videoReactionRepository.findAllByUserAndReaction(u, c);
        List<VideoResponseDTO> likedVideos = new ArrayList<>();
        for (VideoReaction videoReaction : videoReactions) {
            likedVideos.add(videoToResponseVideoDTO(videoReaction.getVideo()));
        }
        return likedVideos;
    }

    public VideoResponseDTO watch(long vid, long loggedUserId) {
        Video video = getVideoById(vid);
        User user = getUserById(loggedUserId);
        if (!user.getWatchedVideos().contains(video)) {
            user.getWatchedVideos().add(video);
            userRepository.save(user);
        }
        VideoResponseDTO vDTO = videoToResponseVideoDTO(video);
        return vDTO;
    }

    public String delete(long vid, long loggedUserId) {
        Video video = getVideoById(vid);
        if (video.getOwner().getId() != loggedUserId) {
            throw new UnauthorizedException("You're not the owner of the video");
        }
        if (s3Client.doesObjectExist(bucketName, video.getVideoURL())) {
            s3Client.deleteObject(bucketName, video.getVideoURL());
            return video.getVideoURL() + " removed";
        }
        return "File not found! Deletion unsuccessful!";
    }

    public List<VideoSimpleResponseDTO> getMostWatched(int rows, int pageNumber) {
        if (pageNumber >= 0 && rows > 0) {
            List<VideoSimpleResponseDTO> videos = videoDAO.getVideosByMostWatched(rows, pageNumber);
            if (videos.isEmpty()) {
                throw new NotFoundException("No videos on this page");
            }
            return videos;
        } else {
            throw new BadRequestException("Invalid parameters");
        }
    }

    public List<VideoSimpleResponseDTO> getMostLiked(int rows, int pageNumber) {
        if (pageNumber >= 0 && rows > 0) {
            List<VideoSimpleResponseDTO> videos = videoDAO.getMostLikedVideos(rows, pageNumber);
            if (videos.isEmpty()) {
                throw new NotFoundException("No videos on this page");
            }
            return videos;
        } else {
            throw new BadRequestException("Invalid parameters");
        }
    }

    public List<VideoResponseDTO> getUploads(long uid) {
        User user = getUserById(uid);
        if (!user.isVerified()) {
            throw new UnauthorizedException("Please verify your account first!You cannot upload any videos if your account is not verified!");
        }

        List<Video> videos = videoRepository.findAllByOwner(user);
        List<VideoResponseDTO> videosDto = videos.stream().map(v -> modelMapper.map(v, VideoResponseDTO.class)).collect(Collectors.toList());
        return videosDto;
    }

    public String deleteViewHistory(long loggedUserId) {
        User user = getUserById(loggedUserId);
        user.getWatchedVideos().clear();
        userRepository.save(user);
        return "History deleted";
    }
}
