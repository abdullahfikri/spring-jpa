package dev.mfikri.jpa.repository;

import dev.mfikri.jpa.entity.Category;
import dev.mfikri.jpa.entity.Product;
import dev.mfikri.jpa.model.ProductPrice;
import dev.mfikri.jpa.model.SimpleProduct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.support.TransactionOperations;

import java.util.List;
import java.util.stream.Stream;

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

    @Test
    void modifying() {
        transactionOperations.executeWithoutResult(transactionStatus -> {
            int total = productRepository.deleteProductUsingName("salah");
            assertEquals(0, total);

            total = productRepository.updateProductPriceToZero(1L);
            assertEquals(1, total);

            Product product = productRepository.findById(1L).orElse(null);
            assertNotNull(product);
            assertEquals(0, product.getPrice());
        });
    }

    @Test
    void stream() {
        transactionOperations.executeWithoutResult(transactionStatus -> {
            Category category = categoryRepository.findById(1L).orElse(null);
            assertNotNull(category);

            Stream<Product> stream = productRepository.streamAllByCategory(category);
            stream.forEach(product -> System.out.println(product.getId() + " : " + product.getName()));
        });
    }

    @Test
    void slice() {
        Pageable firstPage = PageRequest.of(0, 1);
        Category category = categoryRepository.findById(1L).orElse(null);
        assertNotNull(category);
        Slice<Product> slice = productRepository.findAllByCategory(category, firstPage);

        while (slice.hasNext()){
            slice = productRepository.findAllByCategory(category, slice.nextPageable());
            //tampilkan kontent product
        }
    }

    @Test
    void lock1() {
        transactionOperations.executeWithoutResult(transactionStatus -> {
            try {
                Product product = productRepository.findFirstByIdEquals(1L).orElse(null);
                assertNotNull(product);
                product.setPrice(30_000_000L);
                Thread.sleep(20_000L);
                productRepository.save(product);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void lock2() {
        transactionOperations.executeWithoutResult(transactionStatus -> {
            Product product = productRepository.findFirstByIdEquals(1L).orElse(null);
            assertNotNull(product);
            product.setPrice(10_000_000L);
            productRepository.save(product);
        });
    }

    @Test
    void specification() {
        Specification<Product> specification = (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaQuery.where(
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("name"), "Ultramilk 3L Vanilla"),
                            criteriaBuilder.equal(root.get("name"), "Ultramilk 3L Strawberry")
                    )
            ).getRestriction();
        };

        List<Product> products = productRepository.findAll(specification);

        assertEquals(2, products.size());
    }

    @Test
    void projection() {
        List<SimpleProduct> simpleProducts = productRepository.findAllByNameLike("%Ultramilk%", SimpleProduct.class);
        assertEquals(2, simpleProducts.size());

        List<ProductPrice> productPrices = productRepository.findAllByNameLike("Ultramilk%", ProductPrice.class);
        assertEquals(2, productPrices.size());
    }
}