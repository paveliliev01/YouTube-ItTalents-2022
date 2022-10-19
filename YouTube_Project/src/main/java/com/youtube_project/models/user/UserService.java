package com.youtube_project.models.user;

import com.youtube_project.models.util.AbstractService;
import com.youtube_project.models.exceptions.BadRequestException;
import com.youtube_project.models.exceptions.NotFoundException;
import com.youtube_project.models.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public static int USER_NOT_CREATOR_ROLE = 1;
    public static int USER_CREATOR_ROLE = 2;

    public UserResponseDTO register(UserRegistrationDTO userDTO) {
        checkForUserInformation(userDTO);
        User u = modelMapper.map(userDTO, User.class);
        u.setRoleId(USER_NOT_CREATOR_ROLE);
        userRepository.save(u);
        u = userRepository.findAllByEmail(userDTO.getEmail()).get(0);
        return modelMapper.map(u, UserResponseDTO.class);
    }


    public UserResponseDTO getUserDTOById(long id) {
        return modelMapper.map(userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found")), UserResponseDTO.class);
    }

    public UserResponseDTO login(UserLoginWithEmailDTO userLoginWithEmailDTO) {
        String email = userLoginWithEmailDTO.getEmail().trim();
        if (!email.matches(VALID_EMAIL_ADDRESS_REGEX)) {
            throw new BadRequestException("Wrong credentials");
        }
        List<User> user = userRepository.findAllByEmail(userLoginWithEmailDTO.getEmail());
        if (user.size() == 0) {
            throw new NotFoundException("Wrong credentials");
        }
        User u = user.get(0);
        if (!userLoginWithEmailDTO.getPassword().equals(u.getPassword())) {
            throw new UnauthorizedException("Wrong credentials");
        }
        return modelMapper.map(u, UserResponseDTO.class);
    }

    public UserResponseDTO loginWithPhoneNum(UserLoginWithPhoneNumDTO userLoginWithPhoneNumDTO) {
        String phone = userLoginWithPhoneNumDTO.getPhoneNumber().trim();
        if (!phone.matches(VALID_EMAIL_ADDRESS_REGEX)) {
            throw new BadRequestException("Wrong credentials");
        }

        List<User> user = userRepository.findAllByPhoneNumber(userLoginWithPhoneNumDTO.getPhoneNumber());
        if (user.size() == 0) {
            throw new NotFoundException("Wrong credentials");
        }
        User u = user.get(0);
        if (!userLoginWithPhoneNumDTO.getPassword().equals(u.getPassword())) {
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
        u.setAdditionalInformation("DELETED ON " + LocalDateTime.now());
        u.setBackgroundImage("DELETED ON " + LocalDateTime.now());
        u.setPhoneNumber("DELETED");
        u.setEmail("DELETED ON " + LocalDateTime.now());
        u.setFirstName("DELETED ON " + LocalDateTime.now());
        u.setLastName("DELETED ON " + LocalDateTime.now());
        u.setProfilePhoto("DELETED ON " + LocalDateTime.now());
        u.setPassword("DELETED ON " + LocalDateTime.now());
        userRepository.save(u);
        return true;
    }

    public void checkForUserInformation(UserRegistrationDTO userDTO) {

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
    }

    public List<UserResponseDTO> getAllByName(String firstName, String lastName) {
        return userRepository.findAllByFirstNameAndLastName(firstName, lastName).stream().map(u -> modelMapper.map(u, UserResponseDTO.class)).collect(Collectors.toList());
    }

    public UserEditProfileDTO edit(long id, UserEditProfileDTO dto) {
        User u = getUserById(id);
        dto.setId(u.getId());
        u.setFirstName(dto.getFirstName());
        u.setLastName(dto.getLastName());
        u.setDateOfBirth(dto.getDateOfBirth());
        u.setAdditionalInformation(dto.getAdditionalInformation());
        userRepository.save(u);
        return dto;
    }

    public Set<UserResponseDTO> getFollowers(long userId){
        User u = getUserById(userId);
        Set<User> followers = u.getFollowers();
        return followers.stream().map(user -> modelMapper.map(user,UserResponseDTO.class)).collect(Collectors.toSet());
    }
}
