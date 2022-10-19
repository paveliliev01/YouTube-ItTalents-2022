package com.youtube_project.models.user;

import lombok.Data;

@Data
public class UserChangePasswordDTO {

    private String currentPassword;
    private String passwordNew;
    private String passwordNewConfirm;

}

