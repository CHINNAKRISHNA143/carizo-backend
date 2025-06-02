package com.carizo.controller;

import com.carizo.model.Category;
import com.carizo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Get all categories
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // Get category by ID
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));
    }

    // Add or update category (now requires token)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Category addOrUpdateCategory(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestHeader("Authorization") String tokenHeader
    ) {
        String token = tokenHeader.replace("Bearer ", "");
        return categoryService.saveCategoryWithImage(id, name, imageFile, token);
    }

    // Delete category by ID (now requires token)
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id,
                               @RequestHeader("Authorization") String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        categoryService.deleteCategory(id, token);
    }
}
