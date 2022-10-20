package com.youtube_project.contollers;

import com.youtube_project.services.CommentService;
import com.youtube_project.model.exceptions.BadRequestException;
import com.youtube_project.model.exceptions.ErrorDTO;
import com.youtube_project.model.exceptions.NotFoundException;
import com.youtube_project.model.exceptions.UnauthorizedException;
import com.youtube_project.services.PlaylistService;
import com.youtube_project.services.UserService;
import com.youtube_project.services.VideoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

public abstract class MasterController {

    @Autowired
    protected ModelMapper modelMapper;
    @Autowired
    protected SessionManager sessionManager;
    @Autowired
    protected UserService userService;
    @Autowired
    protected CommentService commentService;
    @Autowired
    protected PlaylistService playlistService;
    @Autowired
    protected VideoService videoService;



    private ErrorDTO buildErrorInformation(Exception e, HttpStatus status){
        ErrorDTO dto = new ErrorDTO();
        dto.setStatus(status.value());
        dto.setMsg(e.getMessage());
        dto.setTime(LocalDateTime.now());
        return dto;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    private ErrorDTO handleNotFound(Exception ex){
        return buildErrorInformation(ex,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    private ErrorDTO handleBadRequest(Exception ex){
        return buildErrorInformation(ex,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    private ErrorDTO handleUnauthorizedRequest(Exception ex) {
        return buildErrorInformation(ex,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    private ErrorDTO handleServerError(Exception ex) {
        ex.printStackTrace();
        return buildErrorInformation(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
