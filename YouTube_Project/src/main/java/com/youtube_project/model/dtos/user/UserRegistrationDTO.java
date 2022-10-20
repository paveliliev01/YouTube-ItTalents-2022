package com.youtube_project.model.dtos.user;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserRegistrationDTO {

    private long id;
    private String firstName;
    private String lastName;
    private String password;
    private String confirmPassword;
    private String email;
    private String phoneNumber;
    private String dateOfBirth;
    private int genderId;
    private int roleId;
    private String additionalInfo;

}
