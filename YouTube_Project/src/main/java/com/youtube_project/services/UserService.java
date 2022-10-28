package com.youtube_project.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.youtube_project.model.dtos.user.*;
import com.youtube_project.model.entities.User;
import com.youtube_project.model.exceptions.BadRequestException;
import com.youtube_project.model.exceptions.NotFoundException;
import com.youtube_project.model.exceptions.UnauthorizedException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class UserService extends AbstractService {

    public static final int NAME_MAX_LENGTH = 60;
    public static final String VALID_PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.!])(?=\\S+$).{8,90}$";
    public static final String VALID_EMAIL_ADDRESS_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"; // regex RFC 5322 for Email Validation
    public static final int USER_EMAIL_MAX_LENGTH = 90;
    public static final String USER_VALID_PHONE_NUMBER = "\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";
    public static final int USER_ADDITIONAL_INFO_MAX_LENGTH = 2000;
    public static final int TOTAL_VALID_NUMBER_OF_ROLES = 2; // number of existing roles in DB (consecutive numbers)
    public static final int TOTAL_VALID_NUMBER_OF_GENDERS = 3; // number of existing genders in DB (consecutive numbers)
    public static final String SENDER = "marteeen93@gmail.com";
    public static final int LENGTH = 10;
    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private AmazonS3 s3Client;
    @Value("${application.bucket.name}" + "/profile_pictures")
    private String bucketNameForProfilePictures;
    @Value("${application.bucket.name}" + "/background_picture_user")
    private String bucketNameForProfileBackGroundImage;

    public UserResponseDTO register(UserRegistrationDTO userDTO) {
        // validate user information from json (editable)
        UserEditProfileDTO userEdit = modelMapper.map(userDTO, UserEditProfileDTO.class);
        validateEditableInfo(userEdit);
        // validate additional user info (not editable) (password/confirm_password and e-mail)
        validateRegistrationInfo(userDTO);

        User u = modelMapper.map(userDTO, User.class);
        System.out.println(u.toString());
        u.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        u.setDateOfBirth(LocalDate.parse(userDTO.getDateOfBirth()));
        userRepository.save(u);

        sendEmail(u);

        return modelMapper.map(u, UserResponseDTO.class);
    }

    private void sendEmail(User user) {
        String token = System.currentTimeMillis() + "$$$" + new Random().nextInt(99999) + "*=3214@" + user.getId() + "@" + System.currentTimeMillis();

        new Thread(() -> {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom("marteeen93@gmail.com");
            msg.setTo(user.getEmail());
            msg.setSubject("Verify account");
            msg.setText("You have to verify tour account.\nPlease follow this link: http://localhost:9301/users/verify_registration/" + token);
            javaMailSender.send(msg);
        }).start();
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

        if (!passwordEncoder.matches(loginPassword, u.getPassword())) {
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

        if (!passwordEncoder.matches(userLoginWithPhoneNumDTO.getPassword(), u.getPassword())) {
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

    public List<UserResponseDTO> getAllUsersByName(String firstName, String lastName, int pageNumber, int rowNumbers) {
        Pageable page = PageRequest.of(pageNumber, rowNumbers);
        return userRepository.findAllByFirstNameAndLastName(firstName, lastName, page).stream().map(u -> modelMapper.map(u, UserResponseDTO.class)).collect(Collectors.toList());
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

    private boolean validateEditableInfo(UserEditProfileDTO user) {

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
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Invalid date format!");
        }

        //validate phoneNumber
        if (!phoneNumber.matches(USER_VALID_PHONE_NUMBER)) {
            throw new BadRequestException("Phone number not valid!");
        }
        // check if phone number is registered
        if (userRepository.findAllByPhoneNumber(user.getPhoneNumber()).size() > 0) {
            throw new BadRequestException("USER WITH SUCH PHONE NUMBER ALREADY EXISTS");
        }
        // check if roleId is valid
        boolean validRoleId = false;
        for (int i = 1; i <= TOTAL_VALID_NUMBER_OF_ROLES; i++) {
            if (roleId == i) {
                validRoleId = true;
            }
        }
        if (!validRoleId) {
            throw new BadRequestException("Invalid role!");
        }
        //Validate genderId
        boolean validGenderId = false;
        for (int i = 1; i <= TOTAL_VALID_NUMBER_OF_GENDERS; i++) {
            if (genderId == i) {
                validGenderId = true;
            }
        }
        if (!validGenderId) {
            throw new BadRequestException("Invalid gender!");
        }
        //Additional info validation
        if (additionalInfo.length() > USER_ADDITIONAL_INFO_MAX_LENGTH) {
            throw new BadRequestException("The text is too long!");
        }
        return true;
    }

    private boolean validateRegistrationInfo(UserRegistrationDTO user) {

        String password = user.getPassword();
        String confirmPassword = user.getConfirmPassword();
        String email = user.getEmail();

        //validate password
        if (!password.matches(VALID_PASSWORD_REGEX)) {
            throw new BadRequestException("Invalid password!");
        }
        // check if password match confirm password
        if (!password.equals(confirmPassword)) {
            throw new BadRequestException("Password mismatch!");
        }
        //check if e-mail is valid
        if (email.trim().isEmpty() || email.length() > USER_EMAIL_MAX_LENGTH) {
            throw new BadRequestException("Invalid e-mail!");
        }
        if (!email.matches(VALID_EMAIL_ADDRESS_REGEX)) {
            throw new BadRequestException("Invalid e-mail!");
        }
        //check if email is already registered
        if (userRepository.findAllByEmail(user.getEmail()).size() > 0) {
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
        return modelMapper.map(user, UserEditProfileDTO.class);
    }

    public Set<UserResponseDTO> getSubscriptions(long userId) {
        User u = getUserById(userId);
        Set<User> followers = u.getSubscriptions();
        return followers.stream().map(user -> modelMapper.map(user, UserResponseDTO.class)).collect(Collectors.toSet());
    }

    public List<UserResponseDTO> getFollowers(long userId) {
        User u = getUserById(userId);
        List<User> subscriptions = u.getFollowers();
        return subscriptions.stream().map(user -> modelMapper.map(user, UserResponseDTO.class)).collect(Collectors.toList());
    }


    public String subscribe(long subscribeToId, long uid) {
        User u = getUserById(uid);
        User userToSubscribeTo = getUserById(subscribeToId);
        if (!u.isVerified()){
            throw new UnauthorizedException("Please verify your account first!");
        }
        if (u.getSubscriptions().contains(userToSubscribeTo)) {
            throw new BadRequestException("You are already subscribed to this user");
        }
        u.getSubscriptions().add(userToSubscribeTo);
        userRepository.save(u);
        return "Subscribed to " + userToSubscribeTo.getFirstName();
    }

    public String unsubscribe(long subscribeToId, long uid) {
        User u = getUserById(uid);
        User userToSubscribeTo = getUserById(subscribeToId);
        if (!u.isVerified()){
            throw new UnauthorizedException("Please verify your account first!");
        }
        if (!u.getSubscriptions().contains(userToSubscribeTo)) {
            throw new BadRequestException("You are already unsubscribed from this user");
        }
        u.getSubscriptions().remove(userToSubscribeTo);
        userRepository.save(u);
        return "Unsubscribed from " + userToSubscribeTo.getFirstName();
    }

    public String uploadProfilePhoto(MultipartFile photo, long uid) {
        User user = getUserById(uid);
        String extension = FilenameUtils.getExtension(photo.getOriginalFilename());
        String photoURL = "uploads" + File.separator + "profile_pictures" + File.separator + "profile_photo_user" + "_" + uid + "." + extension;
        saveAndReplacePhoto(photo, user, photoURL, bucketNameForProfilePictures);
        user.setProfilePhoto(photoURL);
        userRepository.save(user);

        return "Successfully uploaded/uploaded your profile photo. Image name: " + photo.getName();
    }

    private File convertMultipartFileToFIle(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            System.out.println("Problem occurred while converting file." + e.getMessage());
        }

        return convertedFile;
    }

    public String uploadBackgroundPhoto(MultipartFile photo, long uid) {
        User user = getUserById(uid);
        String extension = FilenameUtils.getExtension(photo.getOriginalFilename());
        String fileURL = "uploads" + File.separator + "background_pictures" + File.separator + "background_picture_user" + "_" + uid + "." + extension;

        saveAndReplacePhoto(photo, user, fileURL, bucketNameForProfileBackGroundImage);

        user.setBackgroundImage(fileURL);
        userRepository.save(user);

        return "Successfully uploaded/updated your background image. Image name: " + photo.getName();
    }

    private void saveAndReplacePhoto(MultipartFile photo, User user, String photoURL, String bucketName) {
        delete(photoURL, user.getId(), bucketName);
        File file1 = convertMultipartFileToFIle(photo);
        s3Client.putObject(new PutObjectRequest(bucketName, photoURL, file1));
        file1.delete();
    }

    public void delete(String photoURL, long loggedUserId, String bucketName) {
        User u = getUserById(loggedUserId);
        String photo = null;
        if (bucketName.equals(bucketNameForProfilePictures)) {
            photo = u.getProfilePhoto();
        } else {
            photo = u.getBackgroundImage();
        }
        if (photo != null) {

            if (!photo.equals(photoURL)) {
                throw new UnauthorizedException("Wrong photo URL");
            }
            if (s3Client.doesObjectExist(bucketName, photoURL)) {
                s3Client.deleteObject(bucketName, photoURL);
            }
        }

    }

    public UserResponseDTO verifyRegistration(String encryptedId) {
        int id = 0;
        Pattern pattern = Pattern.compile("(?<=@)(.*?)(?=@)");
        Matcher matcher = pattern.matcher(encryptedId);
        if (matcher.find()) {
            id = Integer.parseInt(matcher.group(1));
        }
        User user = getUserById(id);
        user.setVerified(true);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(SENDER);
        msg.setTo(user.getEmail());
        msg.setSubject("Verified");
        msg.setText("You have verified your account");
        javaMailSender.send(msg);
        userRepository.save(user);

        return modelMapper.map(user, UserResponseDTO.class);
    }

    public ResponseEntity<String> forgottenPassword(String email) {
        User user = getUserByEmail(email);
        String newPassword = generateNewPassword(LENGTH);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return sendSMSWithNewPassword(user,newPassword);
    }

    private ResponseEntity<String> sendSMSWithNewPassword(User user,String newPassword) {

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message.creator(new com.twilio.type.PhoneNumber("+359"+ user.getPhoneNumber().substring(1)),
                new com.twilio.type.PhoneNumber("+17432093191"), "Your new password is : " + newPassword).create();

        return new ResponseEntity<>("Message sent successfully", HttpStatus.OK);
    }

    private String generateNewPassword(int length) {
        String password = new Random().ints(length, 33, 122).collect(StringBuilder::new,
                        StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return password;
    }

    public String changePassword(UserChangePasswordDTO dto, long loggedUserId) {
        User user = getUserById(loggedUserId);
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Password mismatch!");
        }
        if (!dto.getPasswordNew().equals(dto.getPasswordNewConfirm())) {
            throw new BadRequestException("Password mismatch!");
        }
        if (!dto.getPasswordNew().matches(VALID_PASSWORD_REGEX)) {
            throw new BadRequestException("Invalid password!");
        }
        user.setPassword(passwordEncoder.encode(dto.getPasswordNew()));
        userRepository.save(user);
        System.out.println(user.getPassword());
        return "Password changed successfully!";
    }

    public String giveAdminPrivilege(long id, long loggedUserId) {
        User user = getUserById(loggedUserId);
        if(!user.isAdmin()){
            throw new UnauthorizedException("Only admin can give admin privilege!");
        }
        User userToBeAdmin = getUserById(id);
        userToBeAdmin.setAdmin(true);
        userRepository.save(userToBeAdmin);
        return userToBeAdmin.getFirstName() + " is now admin.";
    }
}
