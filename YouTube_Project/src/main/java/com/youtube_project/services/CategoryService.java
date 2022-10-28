package com.youtube_project.services;

import com.youtube_project.model.dtos.category.CategoryAddDTO;
import com.youtube_project.model.dtos.category.CategoryDTO;
import com.youtube_project.model.dtos.user.UserResponseDTO;
import com.youtube_project.model.dtos.video.VideoResponseDTO;
import com.youtube_project.model.entities.Category;
import com.youtube_project.model.entities.User;
import com.youtube_project.model.entities.Video;
import com.youtube_project.model.exceptions.BadRequestException;
import com.youtube_project.model.exceptions.NotFoundException;
import com.youtube_project.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryService extends AbstractService{

    public CategoryAddDTO createCategory(CategoryAddDTO categoryAdd,long id) {
        User u = getUserById(id);
        checkIfAdmin(u);
        checkIfCategoryExists(categoryAdd);
        Category category = modelMapper.map(categoryAdd,Category.class);
        category.setCreationDate(LocalDate.now());
        categoryRepository.save(category);
        return modelMapper.map(category,CategoryAddDTO.class);
    }

    private void checkIfCategoryExists(CategoryAddDTO categoryAdd) {
        Optional<Category> category = getCategoryByNameOptional(categoryAdd.getName());
        if(category.isPresent()){
            throw new BadRequestException("Category with such name already exists!");
        }
    }

    public String deleteCategory(CategoryAddDTO categoryDelete,long id) {
        User u = getUserById(id);
        checkIfAdmin(u);
        Optional<Category> category = getCategoryByNameOptional(categoryDelete.getName());
        if(!category.isPresent()){
            throw new NotFoundException("Category with such name doesn't exist");
        }
        else {
            categoryRepository.delete(category.get());
            return "Successfully deleted category " + category.get().getName();
        }
    }

    public String followCategory(String categoryName, long loggedUserId) {
        Category category = getCategoryByName(categoryName);
        User user = getUserById(loggedUserId);
        if(!user.getFollowedCategories().contains(category)){
            user.getFollowedCategories().add(category);
            userRepository.save(user);
            return "User " + user.getFirstName() + " just followed category " + categoryName;
        }
        else {
            throw new BadRequestException("You're already following this category!");
        }
    }

    public String unfollowCategory(String categoryName, long loggedUserId) {
        Category category = getCategoryByName(categoryName);
        User user = getUserById(loggedUserId);
        if(user.getFollowedCategories().contains(category)){
            user.getFollowedCategories().remove(category);
            userRepository.save(user);
            return "User " + user.getFirstName() + " just unfollowed category " + categoryName;
        }
        else {
            throw new BadRequestException("Unable to unfollow, you're not following the category");
        }
    }

    public String addVideoToCategory(String categoryName, long vid,long id) {
        User u = getUserById(id);
        checkIfAdmin(u);
        Video video = getVideoById(vid);
        Category category = getCategoryByName(categoryName);
        if(!video.getCategoriesContainingVideo().contains(category)){
            video.getCategoriesContainingVideo().add(category);
            videoRepository.save(video);
            return "Video added to category " + categoryName;
        }
        else {
            throw new BadRequestException("Video is already in this category!");
        }
    }

    public String removeVideoToCategory(String categoryName, long vid,long id) {
        User u = getUserById(id);
        checkIfAdmin(u);
        Video video = getVideoById(vid);
        Category category = getCategoryByName(categoryName);
        if(video.getCategoriesContainingVideo().contains(category)){
            video.getCategoriesContainingVideo().remove(category);
            videoRepository.save(video);
            return "Video removed from category " + categoryName;
        }
        else {
            throw new BadRequestException("Video is not in the category!");
        }
    }

    public CategoryDTO searchByName(String categoryName) {
        Category category = getCategoryByName(categoryName);
        CategoryDTO categoryDTO = modelMapper.map(category,CategoryDTO.class);
        // map follower -> UserResponseDTO
        Set<UserResponseDTO> followers = category.getFollowers().stream().map(u -> modelMapper.map(u, UserResponseDTO.class)).collect(Collectors.toSet());
        categoryDTO.setFollowers(followers);
        // map video -> VideoResponseDTO
        Set<VideoResponseDTO> videoResponseDTOS = new HashSet<>();
        for (Video vid : category.getVideosInCategory()){
            videoResponseDTOS.add(videoToResponseVideoDTO(vid));
        }
        categoryDTO.setVideos(videoResponseDTOS);
        return categoryDTO;
    }
}
