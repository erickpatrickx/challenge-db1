package com.challenge.service;

import com.challenge.model.Category;
import com.challenge.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private List<Category> categories;

    @BeforeEach
    public void setup() {
        categories = List.of(Category.builder().id(1L).name("Category 1").build(),
                Category.builder().id(2L).name("Category 2").build());
    }

    @Test
    public void testGetAllCategoriesInOrder() {
        when(categoryRepository.findAllByOrderByIdAsc()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategoriesInOrder();

        assertEquals(2, result.size());
        assertEquals("Category 1", result.get(0).getName());
        assertEquals("Category 2", result.get(1).getName());
    }
}