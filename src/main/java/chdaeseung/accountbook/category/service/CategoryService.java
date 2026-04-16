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
    public void createCategory(Long userId, CategoryCreateDto createDto) {
        String categoryName = createDto.getName() == null ? "" : createDto.getName().trim();

        if(categoryName.isEmpty()) {
            throw new CustomException(ErrorCode.BLANK_CATEGORY_NAME);
        }

        if(categoryRepository.existsByUserIdAndName(userId, categoryName)) {
            throw new CustomException(ErrorCode.DUPLICATE_CATEGORY);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Category category = new Category(categoryName, user);

        categoryRepository.save(category);
    }

    public List<CategoryResponseDto> getCategories(Long userId) {
        return categoryRepository.findAllByUserId(userId).stream()
                .map(CategoryResponseDto::from)
                .toList();
    }

    @Transactional
    public void deleteCategory(Long categoryId, Long userId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        if(transactionRepository.existsByCategoryId(categoryId)) {
            throw new CustomException(ErrorCode.CATEGORY_IN_USE);
        }

        categoryRepository.delete(category);
    }

    @Transactional
    public void create(Long userId, CategoryCreateDto dto) {
        if(!StringUtils.hasText(dto.getName())) {
            throw new CustomException(ErrorCode.BLANK_CATEGORY_NAME);
        }

        boolean exists = categoryRepository.existsByUserIdAndName(userId, dto.getName().trim());

        if(exists) {
            throw new CustomException(ErrorCode.DUPLICATE_CATEGORY);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        categoryRepository.save(Category.builder()
                .name(dto.getName().trim())
                .user(user)
                .build());
    }

    @Transactional
    public void delete(Long userId, Long id) {
        checkDelete(userId, id);

        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        categoryRepository.delete(category);
    }

    @Transactional
    public void checkDelete(Long userId, Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        if (!category.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        if (transactionRepository.existsByCategoryId(id)) {
            throw new CustomException(ErrorCode.CATEGORY_IN_USE);
        }
    }
}