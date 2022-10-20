package com.youtube_project.model.dtos.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserEditProfileDTO {

    private long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String dateOfBirth;
    private int genderId;
    private int roleId;
    private String additionalInfo;

}
