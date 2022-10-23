package com.youtube_project.services;

import com.youtube_project.model.dtos.category.CategoryAddDTO;
import com.youtube_project.model.entities.Category;
import com.youtube_project.model.entities.User;
import com.youtube_project.model.exceptions.BadRequestException;
import com.youtube_project.model.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CategoryService extends AbstractService{

    public CategoryAddDTO createCategory(CategoryAddDTO categoryAdd) {
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

    public String deleteCategory(CategoryAddDTO categoryDelete) {
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
}
