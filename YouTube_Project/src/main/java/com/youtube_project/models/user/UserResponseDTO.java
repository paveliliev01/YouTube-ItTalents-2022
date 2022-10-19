package com.youtube_project.models.user;

import lombok.Data;
@Data
public class UserResponseDTO {

    private long id;
    private String firstName;
    private String lastName;
    private String profilePhoto;
    private String additionalInformation;
}
