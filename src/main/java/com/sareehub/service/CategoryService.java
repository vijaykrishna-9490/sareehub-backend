package com.sareehub.service;

import com.sareehub.entity.Category;
import com.sareehub.exception.ApiException;
import com.sareehub.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    public Category update(Long id, Category updated) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new ApiException("Category not found", HttpStatus.NOT_FOUND));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setImageUrl(updated.getImageUrl());
        existing.setSlug(updated.getSlug());
        return categoryRepository.save(existing);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
