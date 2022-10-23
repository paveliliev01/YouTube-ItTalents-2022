package com.youtube_project.services;

import com.youtube_project.model.dtos.category.CategoryAddDTO;
import com.youtube_project.model.entities.Category;
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
        Optional<Category> category = getCategoryByName(categoryAdd.getName());
        if(category.isPresent()){
            throw new BadRequestException("Category with such name already exists!");
        }
    }

    public String deleteCategory(CategoryAddDTO categoryDelete) {
        Optional<Category> category = getCategoryByName(categoryDelete.getName());
        if(!category.isPresent()){
            throw new NotFoundException("Category with such name doesn't exist");
        }
        else {
            categoryRepository.delete(category.get());
            return "Successfully deleted category " + category.get().getName();
        }
    }
}
