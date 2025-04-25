-- sudo mysql -u root;
-- CREATE DATABASE belajar_spring_data_jpa;
-- GRANT ALL PRIVILEGES ON belajar_spring_data_jpa.* TO 'java'@'localhost';

CREATE TABLE categories
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL ,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

DESC categories;

SELECT * FROM categories;

CREATE  TABLE products
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL ,
    price BIGINT NOT NULL ,
    category_id BIGINT NOT NULL ,
    primary key (id),
    foreign key fk_products_categories (category_id) REFERENCES categories(id)
) ENGINE = InnoDB;

SELECT * FROM products;

