package chdaeseung.accountbook.transaction.service;

import chdaeseung.accountbook.bank.entity.BankAccount;
import chdaeseung.accountbook.bank.repository.BankAccountRepository;
import chdaeseung.accountbook.category.entity.Category;
import chdaeseung.accountbook.category.repository.CategoryRepository;
import chdaeseung.accountbook.global.exception.CustomException;
import chdaeseung.accountbook.global.exception.ErrorCode;
import chdaeseung.accountbook.transaction.dto.*;
import chdaeseung.accountbook.transaction.entity.ExpenseType;
import chdaeseung.accountbook.transaction.entity.Transaction;
import chdaeseung.accountbook.transaction.entity.TransactionType;
import chdaeseung.accountbook.transaction.repository.TransactionRepository;
import chdaeseung.accountbook.user.entity.User;
import chdaeseung.accountbook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BankAccountRepository bankAccountRepository;

    public void createTransaction(TransactionRequestDto requestDto, Long userId) {
        validateTransactionRequest(requestDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findByIdAndUserId(requestDto.getCategoryId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        ExpenseType expenseType = resolveExpenseType(requestDto.getType());

        String memo = requestDto.getMemo() == null ? "" : requestDto.getMemo().trim();

        BankAccount bankAccount = getBankAccountOrNull(requestDto.getBankAccountId(), userId);

        if(bankAccount != null) {
            applyBalance(bankAccount, requestDto.getType(), requestDto.getAmount());
        }

        Transaction transaction = Transaction.builder()
                .type(requestDto.getType())
                .expenseType(expenseType)
                .amount(requestDto.getAmount())
                .memo(memo)
                .date(requestDto.getDate())
                .category(category)
                .user(user)
                .recurringTransaction(null)
                .bankAccount(bankAccount)
                .build();

        transactionRepository.save(transaction);
    }

    private BankAccount getBankAccountOrNull(Long bankAccountId, Long userId) {
        if(bankAccountId == null) {
            return null;
        }

        return bankAccountRepository.findByIdAndUserId(bankAccountId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    private void applyBalance(BankAccount bankAccount, TransactionType type, Long amount) {
        if(type == TransactionType.INCOME) {
            bankAccount.increaseBalance(amount);
        } else {
            bankAccount.decreaseBalance(amount);
        }
    }

    private void rollbackBalance(BankAccount bankAccount, TransactionType type, Long amount) {
        if(type == TransactionType.INCOME) {
            bankAccount.decreaseBalance(amount);
        } else {
            bankAccount.increaseBalance(amount);
        }
    }

    public List<TransactionResponseDto> getTransactions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return transactionRepository.findAllByUserOrderByDateDescIdDesc(user)
                .stream()
                .map(TransactionResponseDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransactionDetailResponseDto getTransactionDetail(Long userId, Long transactionId) {
        Transaction transaction = getOwnedTransaction(transactionId, userId);

        return TransactionDetailResponseDto.from(transaction);
    }

    @Transactional(readOnly = true)
    public TransactionRequestDto transactionUpdate(Long transactionId, Long userId) {
        Transaction transaction = getOwnedTransaction(transactionId, userId);

        TransactionRequestDto dto = new TransactionRequestDto();
        dto.setType(transaction.getType());
        dto.setAmount(transaction.getAmount());
        dto.setCategoryId(transaction.getCategory().getId());
        dto.setMemo(transaction.getMemo());
        dto.setDate(transaction.getDate());

        if(transaction.getBankAccount() != null) {
            dto.setBankAccountId(transaction.getBankAccount().getId());
        }

        return dto;
    }

    @Transactional
    public void updateTransaction(Long transactionId, Long userId, TransactionRequestDto requestDto) {
        validateTransactionRequest(requestDto);

        Transaction transaction = getOwnedTransaction(transactionId, userId);

        Category category = categoryRepository.findByIdAndUserId(requestDto.getCategoryId(), userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        ExpenseType expenseType = resolveExpenseType(transaction ,requestDto.getType());

        String memo = requestDto.getMemo() == null ? "" : requestDto.getMemo().trim();

        BankAccount oldBankAccount = transaction.getBankAccount();
        if(oldBankAccount != null) {
            rollbackBalance(oldBankAccount, transaction.getType(), transaction.getAmount());
        }

        BankAccount newBankAccount = getBankAccountOrNull(requestDto.getBankAccountId(), userId);
        if(requestDto.getBankAccountId() != null) {
            applyBalance(newBankAccount, requestDto.getType(), requestDto.getAmount());
        }

        transaction.update(
                requestDto.getType(),
                expenseType,
                requestDto.getAmount(),
                category,
                memo,
                requestDto.getDate(),
                newBankAccount
                );
    }

    private Transaction getOwnedTransaction(Long transactionId, Long userId) {
        return transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND));
    }

    @Transactional
    public void deleteTransaction(Long transactionId, Long userId) {
        Transaction transaction = getOwnedTransaction(transactionId, userId);

        if(!transaction.isTransfer()) {
            if (transaction.getBankAccount() != null) {
                rollbackBalance(transaction.getBankAccount(), transaction.getType(), transaction.getAmount());
            }
            transactionRepository.delete(transaction);
            return;
        }

        if(transaction.getTransferGroupKey() == null || transaction.getTransferGroupKey().isBlank()) {
            throw new CustomException(ErrorCode.TRANSACTION_NOT_FOUND);
        }

        List<Transaction> transferTransactions =
                transactionRepository.findAllByTransferGroupKeyAndUserId(transaction.getTransferGroupKey(), userId);

        for(Transaction transferTransaction : transferTransactions) {
            if(transferTransaction.getBankAccount() != null) {
                rollbackBalance(
                        transferTransaction.getBankAccount(),
                        transferTransaction.getType(),
                        transferTransaction.getAmount()
                );
            }
        }

        transactionRepository.deleteAll(transferTransactions);
    }

    @Transactional(readOnly = true)
    public Page<TransactionListResponseDto> getTransactions(Long userId, TransactionSearchDto searchDto) {
        validateSearchDate(searchDto);

        Pageable pageable = PageRequest.of(searchDto.getPage(), searchDto.getSize());

        Page<Transaction> transactionPage = transactionRepository.searchTransactions(userId, searchDto, pageable);

        return transactionPage.map(TransactionListResponseDto::from);
    }

    private void validateSearchDate(TransactionSearchDto searchDto) {
        if(searchDto.getStartDate() != null && searchDto.getEndDate() != null
            && searchDto.getStartDate().isAfter(searchDto.getEndDate())) {
            throw new CustomException(ErrorCode.CANT_BE_END_DATE);
        }
    }

    private ExpenseType resolveExpenseType(TransactionType type) {
        if (type == TransactionType.INCOME) {
            return null;
        }
        return ExpenseType.VARIABLE;
    }

    private ExpenseType resolveExpenseType(Transaction transaction, TransactionType type) {
        if(type == TransactionType.INCOME) {
            return null;
        }

        if(transaction.getRecurringTransaction() != null) {
            return ExpenseType.FIXED;
        }

        return ExpenseType.VARIABLE;
    }

    private void validateTransactionRequest(TransactionRequestDto requestDto) {
        if(requestDto.getDate() == null) {
            throw new CustomException(ErrorCode.INSERT_DATE);
        }

        if(requestDto.getCategoryId() == null) {
            throw new CustomException(ErrorCode.CHOOSE_CATEGORY);
        }

        if(requestDto.getType() == null) {
            throw new CustomException(ErrorCode.CHOOSE_TYPE);
        }

        if(requestDto.getAmount() == null || requestDto.getAmount() <= 0) {
            throw new CustomException(ErrorCode.MINIMUM_AMOUNT);
        }
    }
}
