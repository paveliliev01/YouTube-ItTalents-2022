package com.youtube_project.models.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRegistrationDTO {

    private String firstName;
    private String lastName;
    private String password;
    private String confirmPassword;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private int genderId;

}
