package com.youtube_project.contollers;

import com.youtube_project.model.dtos.SearchDTO;
import com.youtube_project.model.dtos.user.UserResponseDTO;
import com.youtube_project.model.dtos.video.VideoResponseDTO;
import com.youtube_project.model.entities.User;
import com.youtube_project.model.entities.Video;
import com.youtube_project.services.*;
import com.youtube_project.model.exceptions.BadRequestException;
import com.youtube_project.model.exceptions.ErrorDTO;
import com.youtube_project.model.exceptions.NotFoundException;
import com.youtube_project.model.exceptions.UnauthorizedException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
    @Autowired
    protected CategoryService categoryService;
    @Autowired
    protected SearchService searchService;

    public static final char LIKE = 'l';
    public static final char DISLIKE = 'd';


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
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public SearchDTO search(@RequestParam String name,
                                         @RequestParam(defaultValue = "0") int pageNumber,
                                         @RequestParam(defaultValue = "1") int rowNumbers) {
        Pageable page = PageRequest.of(pageNumber,rowNumbers);
        return searchService.searchByName(name,page);
    }

}
