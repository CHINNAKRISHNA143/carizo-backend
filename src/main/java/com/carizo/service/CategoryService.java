package com.carizo.service;

import com.carizo.model.Category;
import com.carizo.model.User;
import com.carizo.repository.CategoryRepository;
import com.carizo.repository.UserRepository;
import com.carizo.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CategoryService {

    private final String uploadDir = System.getProperty("user.home") + "/uploads/category-images";

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Get all categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Get category by ID
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // Create or Update category with image and vendor check
    public Category saveCategoryWithImage(Long id, String name, MultipartFile imageFile, String token) {
        Category category;

        if (id != null) {
            // Existing category: update
            category = categoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));
        } else {
            // New category: set creator
            category = new Category();
            String email = jwtUtil.extractUsername(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            category.setCreatedBy(user);
        }

        category.setName(name);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String filename = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(filename);
                imageFile.transferTo(filePath.toFile());

                category.setImageUrl("/uploads/category-images/" + filename);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save category image", e);
            }
        }

        return categoryRepository.save(category);
    }

    // Delete category only if current user is the creator
    public void deleteCategory(Long categoryId, String token) {
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to delete this category.");
        }

        categoryRepository.delete(category);
    }

    // Get category by name
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
}
