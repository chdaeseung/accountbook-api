package chdaeseung.accountbook.recurring.service;

import chdaeseung.accountbook.bank.entity.BankAccount;
import chdaeseung.accountbook.bank.repository.BankAccountRepository;
import chdaeseung.accountbook.global.exception.CustomException;
import chdaeseung.accountbook.global.exception.ErrorCode;
import chdaeseung.accountbook.recurring.dto.RecurringTransactionCreateDto;
import chdaeseung.accountbook.recurring.dto.RecurringTransactionResponseDto;
import chdaeseung.accountbook.recurring.entity.RecurringTransaction;
import chdaeseung.accountbook.recurring.repository.RecurringTransactionRepository;
import chdaeseung.accountbook.user.entity.User;
import chdaeseung.accountbook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;

    @Transactional(readOnly = true)
    public List<RecurringTransactionResponseDto> getRecurringTransactions(Long userId) {
        return recurringTransactionRepository.findAllByUserIdOrderByDayOfMonthAsc(userId).stream()
                .map(RecurringTransactionResponseDto::from)
                .toList();
    }

    public Long createRecurringTransaction(Long userId, RecurringTransactionCreateDto createDto) {
        validateRecurring(createDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        BankAccount bankAccount = bankAccountRepository.findByIdAndUserId(createDto.getBankAccountId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        RecurringTransaction recurringTransaction = RecurringTransaction.builder()
                .memo(createDto.getMemo())
                .amount(createDto.getAmount())
                .dayOfMonth(createDto.getDayOfMonth())
                .bankAccount(bankAccount)
                .user(user)
                .build();

        return recurringTransactionRepository.save(recurringTransaction).getId();
    }

    @Transactional(readOnly = true)
    public RecurringTransactionResponseDto getRecurringTransaction(Long userId, Long recurringId) {
        RecurringTransaction recurringTransaction = recurringTransactionRepository.findByIdAndUserId(recurringId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECURRING_NOT_FOND));

        return RecurringTransactionResponseDto.from(recurringTransaction);
    }

    public void updateRecurringTransaction(Long userId, Long recurringId, RecurringTransactionCreateDto createDto) {
        validateRecurring(createDto);

        RecurringTransaction recurringTransaction = recurringTransactionRepository.findByIdAndUserId(recurringId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECURRING_NOT_FOND));

        BankAccount bankAccount = bankAccountRepository.findByIdAndUserId(createDto.getBankAccountId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        recurringTransaction.update(
                createDto.getMemo(),
                createDto.getAmount(),
                createDto.getDayOfMonth(),
                bankAccount
        );
    }

    public void deleteRecurringTransaction(Long userId, Long recurringId) {
        RecurringTransaction recurringTransaction = recurringTransactionRepository.findByIdAndUserId(recurringId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECURRING_NOT_FOND));

        recurringTransactionRepository.delete(recurringTransaction);
    }

    @Transactional(readOnly = true)
    public RecurringTransactionCreateDto getRecurringTransactionUpdate(Long userId, Long recurringId) {
        RecurringTransaction recurringTransaction = recurringTransactionRepository.findByIdAndUserId(recurringId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECURRING_NOT_FOND));

        RecurringTransactionCreateDto dto = new RecurringTransactionCreateDto();
        dto.setMemo(recurringTransaction.getMemo());
        dto.setAmount(recurringTransaction.getAmount());
        dto.setDayOfMonth(recurringTransaction.getDayOfMonth());
        dto.setBankAccountId(recurringTransaction.getBankAccount().getId());

        return dto;
    }

    private void validateRecurring(RecurringTransactionCreateDto createDto) {
        if (createDto.getAmount() == null || createDto.getAmount() <= 0) {
            throw new CustomException(ErrorCode.MINIMUM_AMOUNT);
        }

        if (createDto.getDayOfMonth() == null || createDto.getDayOfMonth() < 1 || createDto.getDayOfMonth() > 31) {
            throw new CustomException(ErrorCode.INCORRECT_DAY);
        }

        if (createDto.getBankAccountId() == null) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
    }
}