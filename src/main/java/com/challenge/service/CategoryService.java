package com.challenge.service;

import com.challenge.model.Category;
import com.challenge.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategoriesInOrder() {
        return categoryRepository.findAllByOrderByIdAsc();

    }
}