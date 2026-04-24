package chdaeseung.accountbook.category.service;

import chdaeseung.accountbook.category.dto.CategoryCreateDto;
import chdaeseung.accountbook.category.dto.CategoryResponseDto;
import chdaeseung.accountbook.category.entity.Category;
import chdaeseung.accountbook.category.repository.CategoryRepository;
import chdaeseung.accountbook.global.exception.CustomException;
import chdaeseung.accountbook.global.exception.ErrorCode;
import chdaeseung.accountbook.transaction.repository.TransactionRepository;
import chdaeseung.accountbook.transaction.service.TransactionService;
import chdaeseung.accountbook.user.entity.User;
import chdaeseung.accountbook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Long create(Long userId, CategoryCreateDto dto) {
        if(!StringUtils.hasText(dto.getName())) {
            throw new CustomException(ErrorCode.BLANK_CATEGORY_NAME);
        }

        String categoryName = dto.getName().trim();

        if(categoryRepository.existsByUserIdAndName(userId, categoryName)) {
            throw new CustomException(ErrorCode.DUPLICATE_CATEGORY);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Category category = Category.builder()
                .name(categoryName)
                .user(user)
                .build();

        return categoryRepository.save(category).getId();
    }

    public List<CategoryResponseDto> getCategories(Long userId) {
        return categoryRepository.findAllByUserId(userId).stream()
                .map(CategoryResponseDto::from)
                .toList();
    }

    @Transactional
    public void delete(Long userId, Long id) {
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        if("이체".equals(category.getName())) {
            throw new CustomException(ErrorCode.CATEGORY_IN_USE);
        }

        if(transactionRepository.existsByCategoryId(id)) {
            throw new CustomException(ErrorCode.CATEGORY_IN_USE);
        }

        categoryRepository.delete(category);
    }
}