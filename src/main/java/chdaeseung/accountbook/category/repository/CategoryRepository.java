package chdaeseung.accountbook.category.repository;

import chdaeseung.accountbook.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUserId(Long userId);

    Optional<Category> findByIdAndUserId(Long id, Long userId);

    List<Category> findByUserIdOrderByNameAsc(Long userId);

    boolean existsByUserIdAndName(Long userId, String name);

    Optional<Category> findByNameAndUserId(String name, Long userId);
}
