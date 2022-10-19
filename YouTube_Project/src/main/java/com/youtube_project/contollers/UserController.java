package com.youtube_project.contollers;

import com.youtube_project.models.exceptions.BadRequestException;
import com.youtube_project.models.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController extends MasterController {


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO register(@RequestBody UserRegistrationDTO userDTO) {
        return userService.register(userDTO);
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
        HttpSession session = request.getSession();
        if (sessionManager.isUserLogged(request)) {
            throw new BadRequestException("You are already logged");
        }
        if (userLoginWithEmailDTO.getEmail() == null || userLoginWithEmailDTO.getPassword() == null) {
            throw new BadRequestException("Wrong credentials");
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
    @ResponseStatus
    public List<UserResponseDTO> getAllByName(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName) {
        return userService.getAllByName(firstName, lastName);
    }

    @PutMapping("/edit")
    public UserEditProfileDTO editProfileInfo(@RequestBody UserEditProfileDTO dto, HttpServletRequest request) {
        return userService.edit(sessionManager.getSessionUserId(request), dto);
    }

    @GetMapping("/followers")
    public Set<UserResponseDTO> getFollowers(HttpServletRequest request){
        sessionManager.validateLogin(request);
        return userService.getFollowers(sessionManager.getSessionUserId(request));
    }


}
