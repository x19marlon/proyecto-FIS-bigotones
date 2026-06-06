package com.openlib.repository;

import com.openlib.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByStatus(String status, Pageable pageable);

    /** Búsqueda por texto (título, autor, isbn) y categoría parcial */
    @Query("SELECT b FROM Book b WHERE b.status = 'APROBADO' " +
           "AND (:q = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "     OR LOWER(b.author) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "     OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "AND (:cat = 'Todas' OR :cat = '' OR LOWER(b.category) LIKE LOWER(CONCAT('%', :cat, '%')))")
    List<Book> searchApproved(@Param("q") String q, @Param("cat") String cat);

    /** Lista todas las categorías únicas (valores crudos de la columna) */
    @Query("SELECT DISTINCT b.category FROM Book b WHERE b.status = 'APROBADO' AND b.category IS NOT NULL")
    List<String> findAllDistinctCategories();

    /** Libros relacionados: misma categoría parcial, excluyendo el libro actual */
    @Query("SELECT b FROM Book b WHERE b.status = 'APROBADO' " +
           "AND b.id <> :excludeId " +
           "AND LOWER(b.category) LIKE LOWER(CONCAT('%', :cat, '%'))")
    List<Book> findRelated(@Param("excludeId") Long excludeId, @Param("cat") String cat, Pageable pageable);

    /** Todos los libros sin importar status (para admin) */
    List<Book> findAllByOrderByIdDesc();
}
