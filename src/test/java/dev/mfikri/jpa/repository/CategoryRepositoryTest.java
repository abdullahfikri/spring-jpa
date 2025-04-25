package dev.mfikri.jpa.repository;

import dev.mfikri.jpa.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void insert() {
        Category category = new Category();
        category.setName("Drinks");

        categoryRepository.save(category);
        assertNotNull(category.getId());
    }

    @Test
    void update() {
        Category category = categoryRepository.findById(1L).orElse(null);
        assertNotNull(category);

        category.setName("Drinks and Milk");
        categoryRepository.save(category);

        category = categoryRepository.findById(1L).orElse(null);
        assertNotNull(category);
        assertEquals("Drinks and Milk", category.getName());
    }

    @Test
    void queryMethod() {
        Category category = categoryRepository.findFirstByNameEquals("Drinks and Milk").orElse(null);
        assertNotNull(category);
        assertEquals("Drinks and Milk", category.getName());

        List<Category> categories = categoryRepository.findAllByNameLike("%Milk%");
        assertEquals(1, categories.size());
        assertEquals("Drinks and Milk", categories.getFirst().getName());
    }


}