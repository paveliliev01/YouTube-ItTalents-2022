package com.youtube_project.contollers;

import com.youtube_project.model.dtos.user.*;
import com.youtube_project.model.dtos.video.VideoResponseDTO;
import com.youtube_project.model.entities.Video;
import com.youtube_project.model.exceptions.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController extends MasterController {


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO register(@RequestBody UserRegistrationDTO userDTO, HttpServletRequest request) {
        if (sessionManager.isUserLogged(request)) {
            throw new BadRequestException("You are already logged.You cannot register while being logged in!!");
        }
        return userService.register(userDTO);
    }

    @GetMapping("/verify_registration/{encryptedId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO verifyRegistration(@PathVariable String encryptedId) {
        return userService.verifyRegistration(encryptedId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.FOUND)
    public UserResponseDTO getUserById(@PathVariable long id) {
        return userService.getUserDTOById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.FOUND)
    public List<UserResponseDTO> getALlUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/auth/email")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserResponseDTO loginWithEmail(@RequestBody UserLoginWithEmailDTO userLoginWithEmailDTO, HttpServletRequest request) {
        if (sessionManager.isUserLogged(request)) {
            throw new BadRequestException("You are already logged");
        }
        UserResponseDTO dto = userService.login(userLoginWithEmailDTO);
        sessionManager.setSession(request, dto.getId());
        return dto;
    }

    @PostMapping("/auth/phone_number")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserResponseDTO loginWithPhoneNumber(@RequestBody UserLoginWithPhoneNumDTO userLoginWithEmailDTO, HttpServletRequest request) {
        if (sessionManager.isUserLogged(request)) {
            throw new BadRequestException("You are already logged");
        }
        UserResponseDTO dto = userService.loginWithPhoneNum(userLoginWithEmailDTO);
        sessionManager.setSession(request, dto.getId());
        return modelMapper.map(dto, UserResponseDTO.class);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public String logout(HttpServletRequest request) {
        sessionManager.validateLogin(request);
        String name = getUserById((long) request.getSession().getAttribute("user_id")).getFirstName();
        request.getSession().invalidate();
        return "User " + name + " logged out";
    }

    @PutMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public boolean deleteAccount(HttpServletRequest request) {
        sessionManager.validateLogin(request);
        return userService.deleteUserAccount(sessionManager.getSessionUserId(request));
    }

    @GetMapping("/getByName")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDTO> getAllByName(@RequestParam("firstName") String firstName,
                                              @RequestParam("lastName") String lastName,
                                              @RequestParam(defaultValue = "0") int pageNumber,
                                              @RequestParam(defaultValue = "5") int rowNumbers) {
        return userService.getAllUsersByName(firstName, lastName, pageNumber, rowNumbers);
    }

    @PutMapping("/edit")
    @ResponseStatus(HttpStatus.OK)
    public UserEditProfileDTO editProfileInfo(@RequestBody UserEditProfileDTO dto, HttpServletRequest request) {
        sessionManager.validateLogin(request);
        return userService.edit(sessionManager.getSessionUserId(request), dto);
    }

    @GetMapping("/followers")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDTO> getFollowers(HttpServletRequest request) {
        sessionManager.validateLogin(request);
        return userService.getFollowers(sessionManager.getSessionUserId(request));
    }

    @GetMapping("/subscriptions")
    @ResponseStatus(HttpStatus.OK)
    public Set<UserResponseDTO> getSubscriptions(HttpServletRequest request) {
        sessionManager.validateLogin(request);
        return userService.getSubscriptions(sessionManager.getSessionUserId(request));
    }

    @PostMapping("/subscribe/{uid}")
    @ResponseStatus(HttpStatus.OK)
    public String subscribeToAUser(@PathVariable long uid, HttpServletRequest request) {
        sessionManager.validateLogin(request);
        return userService.subscribe(uid, sessionManager.getSessionUserId(request));
    }

    @PostMapping("/unsubscribe/{uid}")
    @ResponseStatus(HttpStatus.OK)
    public String unSubscribeFromAUser(@PathVariable long uid, HttpServletRequest request) {
        sessionManager.validateLogin(request);
        return userService.unsubscribe(uid, sessionManager.getSessionUserId(request));
    }

    @PostMapping("/profile_photo")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadProfilePhoto(@RequestParam(value = "photo") MultipartFile photo, HttpServletRequest request){
        sessionManager.validateLogin(request);
        long loggedUserId = sessionManager.getSessionUserId(request);
        return userService.uploadProfilePhoto(photo,loggedUserId);
    }

    @PostMapping("/background_photo")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadBackgroundPhoto(@RequestParam(value = "photo") MultipartFile photo, HttpServletRequest request){
        sessionManager.validateLogin(request);
        long loggedUseId = sessionManager.getSessionUserId(request);
        return userService.uploadBackgroundPhoto(photo,loggedUseId);
    }

    @PutMapping("/forgotten_password")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> forgottenPassword(@RequestParam(value = "email") String email, HttpServletRequest request){
        return userService.forgottenPassword(email);
    }

    @PutMapping("/change_password")
    @ResponseStatus(HttpStatus.OK)
    public String changePassword(@RequestBody UserChangePasswordDTO dto, HttpServletRequest request){
        sessionManager.validateLogin(request);
        long loggedUserId = sessionManager.getSessionUserId(request);
        return userService.changePassword(dto,loggedUserId);
    }


}
