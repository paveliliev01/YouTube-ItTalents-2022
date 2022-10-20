package com.youtube_project.services;

import com.youtube_project.model.dtos.user.*;
import com.youtube_project.model.entities.User;
import com.youtube_project.model.exceptions.BadRequestException;
import com.youtube_project.model.exceptions.NotFoundException;
import com.youtube_project.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService extends AbstractService {

    public static final int NAME_MAX_LENGTH = 60;
    public static final String VALID_PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.!])(?=\\S+$).{8,90}$";
    public static final String VALID_EMAIL_ADDRESS_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"; // regex RFC 5322 for Email Validation
    public static final int USER_EMAIL_MAX_LENGTH = 90;
    public static final String USER_VALID_PHONE_NUMBER =  "\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";
    public static final int USER_ADDITIONAL_INFO_MAX_LENGTH = 2000;
    public static final int TOTAL_VALID_NUMBER_OF_ROLES = 2; // number of existing roles in DB (consecutive numbers)
    public static final int TOTAL_VALID_NUMBER_OF_GENDERS = 3; // number of existing genders in DB (consecutive numbers)

    public UserResponseDTO register(UserRegistrationDTO userDTO) {
        // validate user information from json (editable)
        UserEditProfileDTO userEdit = modelMapper.map(userDTO,UserEditProfileDTO.class);
        validateEditableInfo(userEdit);
        // validate additional user info (not editable) (password/confirm_password and e-mail)
        validateRegistrationInfo(userDTO);

        User u = modelMapper.map(userDTO, User.class);
        System.out.println(u.toString());
        u.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        u.setDateOfBirth(LocalDate.parse(userDTO.getDateOfBirth()));
        userRepository.save(u);
        return modelMapper.map(u, UserResponseDTO.class);
    }
    public UserResponseDTO getUserDTOById(long id) {
        User u = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

        return modelMapper.map(u, UserResponseDTO.class);
    }
    public UserResponseDTO login(UserLoginWithEmailDTO userLoginWithEmailDTO) {
        String email = userLoginWithEmailDTO.getEmail().trim();
        String loginPassword = userLoginWithEmailDTO.getPassword().trim();
        if (!email.matches(VALID_EMAIL_ADDRESS_REGEX)) {
            throw new BadRequestException("Wrong credentials");
        }
        List<User> user = userRepository.findAllByEmail(userLoginWithEmailDTO.getEmail());
        if (user.size() == 0) {
            throw new NotFoundException("Wrong credentials");
        }
        User u = user.get(0);
        System.out.println(loginPassword);
        System.out.println(u.getPassword());

        if(!passwordEncoder.matches(loginPassword,u.getPassword())){
            throw new UnauthorizedException("Wrong credentials");
        }

        return modelMapper.map(u, UserResponseDTO.class);
    }

    public UserResponseDTO loginWithPhoneNum(UserLoginWithPhoneNumDTO userLoginWithPhoneNumDTO) {
        String phone = userLoginWithPhoneNumDTO.getPhoneNumber().trim();
        if (!phone.matches(USER_VALID_PHONE_NUMBER)) {
            throw new BadRequestException("Wrong credentials");
        }

        List<User> user = userRepository.findAllByPhoneNumber(phone);
        if (user.size() == 0) {
            throw new NotFoundException("Wrong credentials");
        }
        User u = user.get(0);

        System.out.println(!passwordEncoder.matches(userLoginWithPhoneNumDTO.getPassword(),u.getPassword()));
        if(!passwordEncoder.matches(userLoginWithPhoneNumDTO.getPassword(),u.getPassword())){
            throw new UnauthorizedException("Wrong credentials");
        }

        return modelMapper.map(u, UserResponseDTO.class);
    }

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.size() == 0) {
            throw new NotFoundException("No users to be shown");
        }
        return users.stream().map(u -> modelMapper.map(u, UserResponseDTO.class)).collect(Collectors.toList());
    }

    public boolean deleteUserAccount(long id) {
        User u = getUserById(id);
        u.setBackgroundImage(id + "_DELETED ON " + LocalDateTime.now());
        u.setPhoneNumber(id + "_DELETED");
        u.setEmail(id + "_DELETED ON " + LocalDateTime.now());
        u.setProfilePhoto(id + "_DELETED ON " + LocalDateTime.now());
        u.setPassword(id + "_DELETED ON " + LocalDateTime.now());
        userRepository.save(u);
        return true;
    }

    private boolean validateEditableInfo(UserEditProfileDTO user){

        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String dateOfBirth = user.getDateOfBirth();
        String phoneNumber = user.getPhoneNumber();
        int roleId = user.getRoleId(); // roleId should be 1 -user or 2 - creator
        int genderId = user.getGenderId();  // genderId 1 - Male, 2 - Female, 3 - Other
        String additionalInfo = user.getAdditionalInfo();
        //validate first name
        if (firstName.trim().isEmpty() || firstName.length() > NAME_MAX_LENGTH) {
            throw new BadRequestException("Invalid first name!");
        }
        // validate last name
        if (lastName.trim().isEmpty() || lastName.length() > NAME_MAX_LENGTH) {
            throw new BadRequestException("Invalid last name!");
        }
        // validate date of birth - format : yyyy-MM-dd
        try {
            LocalDate dateOfBirthFormatted = LocalDate.parse(dateOfBirth);
            if (dateOfBirth.trim().isEmpty()) {
                throw new BadRequestException("Invalid Date!");
            }
            if (dateOfBirthFormatted.isAfter(LocalDate.now().minusYears(13))) {
                throw new UnauthorizedException("You should be at least 13 years old");
            }
        }
        catch (DateTimeParseException ex){
            throw new BadRequestException("Invalid date format!");
        }

        //validate phoneNumber
        if(!phoneNumber.matches(USER_VALID_PHONE_NUMBER)){
            throw new BadRequestException("Phone number not valid!");
        }
        // check if phone number is registered
        if (userRepository.findAllByPhoneNumber(user.getPhoneNumber()).size() > 0) {
            throw new BadRequestException("USER WITH SUCH PHONE NUMBER ALREADY EXISTS");
        }
        // check if roleId is valid
        boolean validRoleId = false;
        for (int i = 1; i <= TOTAL_VALID_NUMBER_OF_ROLES; i++) {
            if(roleId == i){
                validRoleId = true;
            }
        }
        if(!validRoleId){
            throw new BadRequestException("Invalid role!");
        }
        //Validate genderId
        boolean validGenderId = false;
        for (int i = 1; i <= TOTAL_VALID_NUMBER_OF_GENDERS; i++) {
            if(genderId == i){
                validGenderId = true;
            }
        }
        if(!validGenderId){
            throw new BadRequestException("Invalid gender!");
        }
        //Additional info validation
        if (additionalInfo.length() > USER_ADDITIONAL_INFO_MAX_LENGTH) {
            throw new BadRequestException("The text is too long!");
        }
        return true;
    }

    private boolean validateRegistrationInfo(UserRegistrationDTO user){

        String password = user.getPassword();
        String confirmPassword = user.getConfirmPassword();
        String email = user.getEmail();

        //validate password
        if (!password.matches(VALID_PASSWORD_REGEX)) {
            throw new BadRequestException("Invalid password!");
        }
        // check if password match confirm password
        if(!password.equals(confirmPassword)){
            throw new BadRequestException("Password mismatch!");
        }
        //check if e-mail is valid
        if(email.trim().isEmpty() || email.length() > USER_EMAIL_MAX_LENGTH){
            throw new BadRequestException("Invalid e-mail!");
        }
        if(!email.matches(VALID_EMAIL_ADDRESS_REGEX)){
            throw new BadRequestException("Invalid e-mail!");
        }
        //check if email is already registered
        if(userRepository.findAllByEmail(user.getEmail()).size() > 0){
            throw new BadRequestException("Email already registered!");
        }
        return true;
    }

    public UserEditProfileDTO edit(long id, UserEditProfileDTO dto) {

        validateEditableInfo(dto);
        User user = getUserById(id);

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
        user.setGenderId(dto.getGenderId());
        user.setRoleId(dto.getRoleId());
        user.setAdditionalInfo(dto.getAdditionalInfo());
        userRepository.save(user);
        return modelMapper.map(user,UserEditProfileDTO.class);
    }

  /*  public void checkForUserInformation(UserRegistrationDTO userDTO) {

        String email = userDTO.getEmail().trim();
        String firstName = userDTO.getFirstName().trim();
        String lastName = userDTO.getLastName().trim();
        LocalDate dateOfBirth = userDTO.getDateOfBirth();
        String phoneNumber = userDTO.getPhoneNumber().trim();
        String password = userDTO.getPassword();

        if (firstName.length() > NAME_MAX_LENGTH || lastName.length() > NAME_MAX_LENGTH){
            throw new BadRequestException("Invalid name length");
        }
        if (!email.matches(VALID_EMAIL_ADDRESS_REGEX) || email.length() > USER_EMAIL_MAX_LENGTH || email.isEmpty() || email.trim().isEmpty()) {
            throw new BadRequestException("Invalid email");
        }
        if (!password.matches(VALID_PASSWORD_REGEX)) {
            throw new BadRequestException("Bad password");
        }
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            throw new BadRequestException("PASSWORD MISMATCH");
        }
        if (userRepository.findAllByEmail(userDTO.getEmail()).size() > 0) {
            throw new BadRequestException("USER WITH SUCH EMAIL ALREADY EXISTS");
        }
        if (phoneNumber.matches(USER_VALID_PHONE_NUMBER) || userRepository.findAllByPhoneNumber(userDTO.getPhoneNumber()).size() > 0) {
            throw new BadRequestException("USER WITH SUCH PHONE NUMBER ALREADY EXISTS");
        }
        if (dateOfBirth == null){
            throw new BadRequestException("YOU SHOULD SELECT YOUR DATE OF BIRTH");
        }
        if (dateOfBirth.isAfter(LocalDate.now().minusYears(13))) {
            throw new UnauthorizedException("You should be at least 13 years old");
        }
    }*/

    public List<UserResponseDTO> getFollowers(long userId){
        User u = getUserById(userId);
        List<User> followers = u.getFollowers();
        return followers.stream().map(user -> modelMapper.map(user,UserResponseDTO.class)).collect(Collectors.toList());
    }
}