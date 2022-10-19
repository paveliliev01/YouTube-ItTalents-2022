package com.youtube_project.models.user;

import lombok.Data;

@Data
public class UserLoginWithEmailDTO {

    private String email;
    private String password;

}
