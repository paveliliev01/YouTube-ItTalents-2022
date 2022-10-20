package com.youtube_project.model.dtos.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDTO {

    private long id;
    private String firstName;
    private String lastName;
    private String profilePhoto;
    private String additionalInformation;
}
