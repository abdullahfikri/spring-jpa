# Pengenalan Spring Data JPA
- Merupakan fitur yang mempermudah pembuatan aplikasi menggunakan Java Persistence API

# Spring Data
- Spring Data JPA merupakan bagian dari Spring Data Project
- Spring data merupakan projek yang mempermudah aplikasi spring dalam manipulasi data di database
- Konsep yang digunakan di tiap project Spring data mempunyai karakteristik yang hampir sama. 
- Sehingga ketika kita sudah belajar Spring Data JPA, kita akan mudah beradaptasi dengan spring data project lainnya.

# Data Source
- Kemudahan dari Spring Data JPA dapat dirasakan ketika pertamakali menjalankan project,
- yang mana kita tidak perlu melakukan konfigurasi yang sangat banyak,
- seperti yang dilakukan ketika menggunakan JPA.
- Seperti halnya untuk kasus Data Source, yang dimana spring boot sudah melakukan konfigurasi secara otomatis di file yang bernama **DataSourceAutoConfiguration**.
- Kita bisa menggunakan application.properties untuk mengubah konfigurasi Data Source bawaan Spring Boot.
- Kita bisa melakukannya dengan konfigurasinya menggunakan prefix spring.datasource.*

# Konfigurasi JPA
- Kita tidak perlu membuat file konfigurasi xml untuk melakukan konfigurasi pada JPA
- Secara otomatis JPA akan menggunakan Data Source Spring dan jika kita ingin menambahkan konfigurasi,
- kita dapat melakukannya melalui application.properties dengan prefix spring.jpa.*

# Entity Manager Factory
- Spring boot juga secara otomatis membuatkan bean Entity Manager Factory sehingga kita tidak perlu membuatnya secara manual.
- Bean Entity Manager Factory akan secara otomatis dibuatkan oleh spring pada file HibernateJpaAutoConfiguration
- #File -> EntityManagerTest

# Repository
- Ketika menggunakan Spring JPA, kita akan jarang sekali menggunakan Entity Manager.
- Spring Data membawa konsep Repository yang merupakan layer untuk mengelola data (ke/dari database) yang konsepnya diambil dari buku Domain Driven Design.
- Setiap entity atau representasi data akan dibuatkan Repositorynya atau tempat mengelola datanya.
- Repository pada string berbentuk interface yang akan secara otomatis diimplementasikan oleh spring menggunakan AOP
- Untuk membuat repository kita perlu membuat interface yang mengimplementasi interface JPARepository<T, ID>
- Kita juga dapat menambahkan anotasi @Repository untuk menandakan bahwa interface tersebut adalah repository, walaupun tidak wajib
- #File -> Category, CategoryRepository

# Category Repository
- JpaRepository  merupakan turunan dari interface CrudRepository dan ListCrudRepository, di mana method tersebut memiliki banyak method untuk melakukan operasi CRUD.
- Sehingga kita tidak perlu menggunakan Entity Manager untuk melakukan CRUD, cukup menggunakan JpaRepository.
- Pada JpaRepository, operasi CREATE dan UPDATE digabung dalam satu method yaitu save().
- #File -> CategoryRepositoryTest

# Declarative Transaction
- Pada spring terdapat fitur declarative transaction, yang mana kita dapat menggunakan fitur transaction pada method menggunakan anotasi @Transactional.
- Anotasi ini secara otomatis akan dibaca oleh Spring AOP dan menjalankan transaction secara otomatis.
- Perlu diperhatikan bahwa spring AOP hanya bisa diaktifkan ketika method tersebut dipanggil dari luar object classnya. 
- Jika dipanggil oleh method pada object yang sama, maka spring AOP tidak akan berjalan, 
- sehingga method yang memiliki anotasi @Transactional jika dipanggil oleh sesama method pada object yang sama maka fitur transactional tersebut tidak akan berjalan.
- #File -> CategoryService, CategoryServiceTest

# Transaction Propagation
- Saat kita menjalankan method yang ada anotasi @Transactional, kita mungkin akan memanggil method lain pada object lain yang menggunakan anotasi @Transactional juga.
- Oleh karena itu kita sebaiknya mengerti tentang attribute propagation pada anotasi @Transactional, untuk kasus seperti yang di atas.
- #File -> CategoryService


# Programmatic Transaction
- Untuk kasus seperti method yang dijalankan secara async maka kita tidak dapat menggunakan anotasi @Transactional untuk membuat transaksi di dalamnya.
- Kita dapat menggunakan cara lama, yaitu menggunakan EntityManager, atau menggunakan fitur yang telah disediakan oleh Sprign, yaitu:
- 1. Transaction Operations
    - Kita dapat menggunakan Bean TransactionOperations untuk membuat transaksi
    - #File -> CategoryService.createCategories(), CategoryServiceTest.programmatic()
- 2. Platform Transaction Manager
    - Bean ini digunakan untuk manajemen transaction secara low level dan manual
    - #File -> CategoryService.manual(), CategoryServiceTest.manual()

# Query Method
- Dengan menggunakan Spring JPA, kita dapat membuat JPA Query Language secara otomatis, hanya dengan menggunakan nama methodnya.
- Spring data akan menerjemahkan nama methodnya menjadi sebuat JPA Query Language
- Terdapat format nama untuk membuat JPA QL secara otomatis.
- untuk query yang mengembalikan data lebih dari satu, kita bisa menggunakan nama prefix findAll...
- untuk query data pertama menggunakan prefix, findFirst...
- dan masih banyak lagi, untuk lebih lengkap dapat kunjungi laman -> https://docs.spring.io/spring-data/jpa/reference/repositories/query-keywords-reference.html
- #File -> CategoryRepository, CategoryRepositoryTest

# Query Relation
- Untuk melakukan query relation menggunakan spring JPA, kita bisa memanfaatkan underscore(_) pada nama methodnya, karena pada nama method kita tidak bisa menggunakan titik(.)
- Seperti ProductRepository.findAllByCategory_Name(String name)
- #File -> Product, ProductRepository.findAllByCategory_Name(String name), ProductRepositoryTest.createProduct(), ProductRepositoryTest.findByCategoryName()

# Sorting
- Fitur pada spring JPA untuk melakukan sorting dengan menambahkan parameter Sort pada posisi terakhir.
- #File -> ProductRepository.findAllByCategory_Name(String name, Sort sort), ProductRepositoryTest.findProductsSort()

# Pagging
- Selain sort spring JPA juga mendukung paging.
- Untuk melakukan pagging kita dapat menambahkan parameter Pageable di posisi terakhir dari methodnya.
- #File -> ProductRepository.findAllByCategory_Name(String name, Pageable pageable), ProductRepositoryTest.findProductsWithPageable()

# Page Result
- Saat kita melakukan pagging, kita tentu ingin mendapatkan informasi seputar pagging tersebut. 
- Seperti jumlah pagenya, current page, total data, dan total page.
- Spring JPA memiliki fitur return value berupa Page<T> yang menghasilkan data query  dan data pagging.
- #File -> ProductRepository.findAllByCategory_Name(String name, Pageable pageable), ProductRepositoryTest.findProductsWithPageable()

# Count Query Method
- JPA Repository juga dapat digunakan untuk membuat count query method.
- Dengan cukup menamai method dengan prefix countBy...   dan selebihnya format query seperti biasanya.
- #File -> ProductRepository.countByCategory_Name(String name),  ProductRepositoryTest.count()

# Exist Query Method
- Method ini mengembalikan nilai boolean tergantung apakah data hasil query tersebut ada atau tidak ada.
- Untuk membuatnya bisa menggunakan prefix existsBy...
- #File -> ProductRepository.existsByName(String nameProduct), ProductRepositoryTest.exists()

# Delete Query Method
- Query Method juga dapat menghapus data sesuai querynya.
- Return dari query method ini dapat berupa int, dan merupakan hasil dari banyaknya record yang berhasil di delete.
- Untuk membuatnya dapat menggunakan nama dengan prefix deleteBy...
- #File -> ProductRepository.deleteByName(String name), ProductRepositoryTest.deleteOld()

# Repository Transaction
- Default saat kita membuat repository interface, spring akan membuat sebuat instance yang berupa turunan dari SimpleJpaRepository.
- Oleh karena itu saat kita melakukan CRUD kita tidak perlu melakukan transaction di dalamnya, karena anotasi @Transactional sudah di tambahkan pada class SimpleJpaRepository.
- Namun perlu diketahui default transaction yang ada pada SimpleJpaRepository bersifat read only. Namun ada beberapa method bawaan SimpleJpaRepository yang di override menjadi @Transactional saja, seperti method save, delete, dan lain-lain.
- Sehingga jika kita ingin membuat method baru untuk mengubah data, kita dapat menambahkan anotasi @Transactional pada method tersebut.
- Perlu diperhatikan ketika kita mengeksekusi beberapa method yang memiliki anotasi @Transaction di luar atau tanpa transaction, maka method-method tersebut berjalan pada transactionnya masing-masing,
- sehingga jika salah satu gagal tidak akan berimpact rollback pada method yang lain.
- #File -> ProductRepository.deleteByName(String name), ProductRepositoryTest.deleteNew()

# Named Query
- Untuk menggunakan Named Query, kita dapat menggunakan anotasi @NamedQuery seperti yang ada pada JPA.
- Nama named query harus disesuaikan dengan nama method yang akan memanggilnya di repository.
- Named query tidak dapat mendukung sort.
- Namun mendukung Pageable tanpa sort. Sehingga jika kita memang ingin melakukan sorting, kita perlu melakukannya secara manual pada querynya.
- #File -> Product, ProductRepository.searchProductUsingName(@Param("name") String name, Pageable pageable), ProductRepositoryTest.searchUsingName()

# Query Annotation
- Query method itu cocok untuk melakukan query yang sederhana yang tidak terlalu kompleks.
- Untuk melakukan query yang kompleks, seperti memerlukan parameter yang banyak maka nama pada method query dapat sangat panjang dan menyulitkan kita untuk membuatnya.
- Oleh karena itu spring JPA menyyediakan anotasi @Query untuk membuat JPA Query Language atau Native Query.
- Query anotasi juga mendukung sort dan paging
- Selain itu anotasi query juga mendukung page results, namun kita perlu manambahkan query untuk menghitung pagging pada parameter countQuery yang ada di anotasi @Query
- #File -> ProductRepository.searchProducts(@Param("name") String name, Pageable pageable), ProductRepositoryTest.searchProductLike()

# Modifying
# Stream
# Slice
# Locking
# Auditing
# Example
# Specification
# Projection