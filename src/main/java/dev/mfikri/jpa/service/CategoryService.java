package dev.mfikri.jpa.service;

import dev.mfikri.jpa.entity.Category;
import dev.mfikri.jpa.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionOperations;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionOperations transactionOperations;
    private final PlatformTransactionManager platformTransactionManager;



    public CategoryService(CategoryRepository categoryRepository, TransactionOperations transactionOperations, PlatformTransactionManager platformTransactionManager) {
        this.categoryRepository = categoryRepository;
        this.transactionOperations = transactionOperations;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void create() {
        for (int i = 0; i < 5; i++) {
            Category category = new Category();
            category.setName("Category -" + i);
            categoryRepository.save(category);
        }
        throw new RuntimeException("Ups rollback please");
    }

    public void test() {
        // transaction will not work because call from same class
//        create();
    }

    public void error () {
        throw new RuntimeException("ups");
    }

    public void createCategories () {
        transactionOperations.executeWithoutResult(transactionStatus -> {
            for (int i = 0; i < 5; i++) {
                Category category = new Category();
                category.setName("Category-" + i);
                categoryRepository.save(category);
            }
            error();
        });
    }

    public void manual () {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setTimeout(10);
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus transaction = platformTransactionManager.getTransaction(definition);

        try {

            for (int i = 0; i < 5; i++) {
                Category category = new Category();
                category.setName("Category-" + i);
                categoryRepository.save(category);
            }
            error();
            platformTransactionManager.commit(transaction);
        } catch (Throwable throwable) {
            platformTransactionManager.rollback(transaction);
            throw  throwable;
        }

    }

}
