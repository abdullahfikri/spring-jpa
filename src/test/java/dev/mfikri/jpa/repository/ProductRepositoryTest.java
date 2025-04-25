package dev.mfikri.jpa.repository;

import dev.mfikri.jpa.entity.Category;
import dev.mfikri.jpa.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.support.TransactionOperations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionOperations transactionOperations;

    @Test
    void createProduct() {
        Category category = categoryRepository.findById(1L).orElse(null);
        assertNotNull(category);
        {
            Product product = new Product();
            product.setName("Ultramilk 3L Vanilla");
            product.setPrice(100_000L);
            product.setCategory(category);
            productRepository.save(product);
            assertNotNull(product.getId());
        }
        {
            Product product = new Product();
            product.setName("Ultramilk 3L Strawberry");
            product.setPrice(120_000L);
            product.setCategory(category);
            productRepository.save(product);
            assertNotNull(product.getId());
        }
    }

    @Test
    void findByCategoryName() {

        List<Product> products = productRepository.findAllByCategory_Name("Drinks and Milk");
        assertEquals(2, products.size());
        assertEquals("Ultramilk 3L Vanilla", products.get(0).getName());
        assertEquals("Ultramilk 3L Strawberry", products.get(1).getName());
    }

    @Test
    void findProductsSort() {
        Sort sort = Sort.by(Sort.Order.desc("id"));
        List<Product> products = productRepository.findAllByCategory_Name("Drinks and Milk", sort);
        assertEquals(2, products.size());
        assertEquals("Ultramilk 3L Strawberry", products.get(0).getName());
        assertEquals("Ultramilk 3L Vanilla", products.get(1).getName());
    }

    @Test
    void findProductsWithPageable() {
        // page 0
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Order.desc("id")));
        Page<Product> products = productRepository.findAllByCategory_Name("Drinks and Milk", pageable);
        assertEquals(1, products.getContent().size());
        assertEquals(0, products.getNumber());
        assertEquals(2, products.getTotalElements());
        assertEquals(2, products.getTotalPages());
        assertEquals("Ultramilk 3L Strawberry", products.getContent().get(0).getName());

        // page 1
        pageable = PageRequest.of(1, 1, Sort.by(Sort.Order.desc("id")));
        products = productRepository.findAllByCategory_Name("Drinks and Milk", pageable);
        assertEquals(1, products.getContent().size());
        assertEquals(1, products.getNumber());
        assertEquals(2, products.getTotalElements());
        assertEquals(2, products.getTotalPages());
        assertEquals("Ultramilk 3L Vanilla", products.getContent().get(0).getName());
    }

    @Test
    void count() {
        long count = productRepository.count();
        assertEquals(2, count);

        count = productRepository.countByCategory_Name("NOT_EXISTED");
        assertEquals(0, count);

        count = productRepository.countByCategory_Name("Drinks and Milk");
        assertEquals(2, count);
    }

    @Test
    void exists() {
        boolean exists = productRepository.existsByName("Ultramilk 3L Vanilla");
        assertTrue(exists);

        exists = productRepository.existsByName("INDOMILK SALAH");
        assertFalse(exists);
    }

    @Test
    void deleteOld() {
        transactionOperations.executeWithoutResult(transactionStatus -> { // transaksi 1
            Category category = categoryRepository.findById(1L).orElse(null);
            assertNotNull(category);

            Product product = new Product();
            product.setName("Kratingdaeng m9");
            product.setPrice(10_000L);
            product.setCategory(category);
            productRepository.save(product); // transaksi 1

            int delete = productRepository.deleteByName("Kratingdaeng m9"); // transaksi 1
            assertEquals(1, delete);

            delete = productRepository.deleteByName("Kratingdaeng m9"); // transaksi 1
            assertEquals(0, delete);



        });
    }

    @Test
    void deleteNew() {
            Category category = categoryRepository.findById(1L).orElse(null);
            assertNotNull(category);

            Product product = new Product();
            product.setName("Kratingdaeng m9");
            product.setPrice(10_000L);
            product.setCategory(category);
            productRepository.save(product); // transaksi 1

            int delete = productRepository.deleteByName("Kratingdaeng m9"); // transaksi 2
            assertEquals(1, delete);

            delete = productRepository.deleteByName("Kratingdaeng m9"); // transaksi 3
            assertEquals(0, delete);
    }

    @Test
    void searchUsingName() {
        Pageable pageable = PageRequest.of(0, 1);
        List<Product> products = productRepository.searchProductUsingName("Ultramilk 3L Vanilla", pageable);
        assertEquals(1, products.size());
        assertEquals("Ultramilk 3L Vanilla", products.get(0).getName());
    }

    @Test
    void searchProductLike() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Order.desc("id")));

        Page<Product> products = productRepository.searchProducts("%milk%", pageable);
        assertEquals(1, products.getContent().size());
        assertEquals(0, products.getNumber());
        assertEquals(2, products.getTotalPages());
        assertEquals(2, products.getTotalElements());

        products = productRepository.searchProducts("%Drinks%", pageable);
        assertEquals(1, products.getContent().size());
        assertEquals(0, products.getNumber());
        assertEquals(2, products.getTotalPages());
        assertEquals(2, products.getTotalElements());
    }
}