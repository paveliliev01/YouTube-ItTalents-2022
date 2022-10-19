package com.youtube_project.models.user;

import lombok.Data;

@Data
public class UserLoginWithPhoneNumDTO {

    private String phoneNumber;
    private String password;

}
