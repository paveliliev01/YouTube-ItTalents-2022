package com.youtube_project.model.dtos.video;

import com.youtube_project.model.dtos.user.UserResponseWithSubscribersAndVideosCountDTO;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoSimpleResponseDTO {
    private int id;
    private String title;
    private UserResponseWithSubscribersAndVideosCountDTO user;
    private LocalDate uploadDate;
    private String videoUrl;
    private long views;
    private long likes;

}