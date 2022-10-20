package com.youtube_project.model.dtos.user;

import lombok.Data;

@Data
public class UserLoginWithPhoneNumDTO {

    private String phoneNumber;
    private String password;

}
