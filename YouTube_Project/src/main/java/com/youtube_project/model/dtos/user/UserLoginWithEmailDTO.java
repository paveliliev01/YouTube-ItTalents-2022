package com.youtube_project.model.dtos.user;

import lombok.Data;

@Data
public class UserLoginWithEmailDTO {

    private String email;
    private String password;

}
