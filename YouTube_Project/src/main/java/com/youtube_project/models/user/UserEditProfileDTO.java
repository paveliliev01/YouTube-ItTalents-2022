package com.youtube_project.models.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserEditProfileDTO {

    private long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private int genderId;
    private String additionalInformation;

}
