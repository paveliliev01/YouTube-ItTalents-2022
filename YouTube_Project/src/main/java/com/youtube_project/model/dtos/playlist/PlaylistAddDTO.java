package com.youtube_project.model.dtos.playlist;

import lombok.Data;

@Data
public class PlaylistAddDTO {

    private long id;
    private String name;
    private boolean isPrivate;

}
