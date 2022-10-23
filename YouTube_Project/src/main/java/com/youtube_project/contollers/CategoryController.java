package com.youtube_project.contollers;

import com.youtube_project.model.dtos.category.CategoryAddDTO;
import com.youtube_project.model.dtos.category.CategoryDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/categories")
public class CategoryController extends MasterController{

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryAddDTO createCategory(@RequestBody CategoryAddDTO categoryAdd, HttpServletRequest request){
        sessionManager.validateLogin(request);
        return categoryService.createCategory(categoryAdd);
    }

    @PutMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public String deleteCategory(@RequestBody CategoryAddDTO categoryDelete, HttpServletRequest request){
        sessionManager.validateLogin(request);
        return categoryService.deleteCategory(categoryDelete);
    }

    @PostMapping("/follow")
    @ResponseStatus(HttpStatus.OK)
    public String followCategory(@RequestParam("name") String categoryName, HttpServletRequest request){
        sessionManager.validateLogin(request);
        long loggedUserId = sessionManager.getSessionUserId(request);
        return categoryService.followCategory(categoryName,loggedUserId);
    }

    @PostMapping("/unfollow")
    @ResponseStatus(HttpStatus.OK)
    public String unfollowCategory(@RequestParam("name") String categoryName, HttpServletRequest request){
        sessionManager.validateLogin(request);
        long loggedUserId = sessionManager.getSessionUserId(request);
        return categoryService.unfollowCategory(categoryName,loggedUserId);
    }

    @PostMapping("/{vid}/add")
    @ResponseStatus(HttpStatus.OK)
    public String addVideoToCategory(@RequestParam("name") String categoryName,@PathVariable long vid, HttpServletRequest request){
        sessionManager.validateLogin(request);
        return categoryService.addVideoToCategory(categoryName,vid);
    }

    @PostMapping("/{vid}/remove")
    @ResponseStatus(HttpStatus.OK)
    public String removeVideoToCategory(@RequestParam("name") String categoryName,@PathVariable long vid, HttpServletRequest request){
        sessionManager.validateLogin(request);
        return categoryService.removeVideoToCategory(categoryName,vid);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO searchByName(@RequestParam("name") String categoryName){
        return categoryService.searchByName(categoryName);
    }

}
