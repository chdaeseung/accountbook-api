package chdaeseung.accountbook.transfer.service;

import chdaeseung.accountbook.bank.entity.BankAccount;
import chdaeseung.accountbook.bank.repository.BankAccountRepository;
import chdaeseung.accountbook.category.entity.Category;
import chdaeseung.accountbook.category.repository.CategoryRepository;
import chdaeseung.accountbook.global.exception.CustomException;
import chdaeseung.accountbook.global.exception.ErrorCode;
import chdaeseung.accountbook.transaction.entity.ExpenseType;
import chdaeseung.accountbook.transaction.entity.Transaction;
import chdaeseung.accountbook.transaction.entity.TransactionType;
import chdaeseung.accountbook.transaction.repository.TransactionRepository;
import chdaeseung.accountbook.transfer.dto.TransferCreateRequestDto;
import chdaeseung.accountbook.transfer.dto.TransferUpdateRequestDto;
import chdaeseung.accountbook.user.entity.User;
import chdaeseung.accountbook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TransferService {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public String createTransfer(TransferCreateRequestDto requestDto, Long userId) {
        validateTransferRequest(requestDto.getDate(), requestDto.getFromBankAccountId(), requestDto.getToBankAccountId(), requestDto.getAmount());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        BankAccount fromAccount = getOwnedBankAccount(requestDto.getFromBankAccountId(), userId);
        BankAccount toAccount = getOwnedBankAccount(requestDto.getToBankAccountId(), userId);

        validateDifferentAccounts(fromAccount.getId(), toAccount.getId());
        validateSufficientBalance(fromAccount, requestDto.getAmount());

        Category transferCategory = getTransferCategory(userId);

        String memo = requestDto.getMemo() == null ? "" : requestDto.getMemo().trim();
        String transferGroupKey = UUID.randomUUID().toString();

        Transaction withdrawTransaction = createWithdrawTransaction(requestDto, user, transferCategory, fromAccount, memo, transferGroupKey);
        Transaction depositTransaction = createDepositTransaction(requestDto, user, transferCategory, toAccount, memo, transferGroupKey);

        fromAccount.decreaseBalance(requestDto.getAmount());
        toAccount.increaseBalance(requestDto.getAmount());

        transactionRepository.save(withdrawTransaction);
        transactionRepository.save(depositTransaction);

        return transferGroupKey;
    }

    @Transactional
    public void updateTransfer(Long transactionId, Long userId, TransferUpdateRequestDto requestDto) {
        validateTransferRequest(requestDto.getDate(), requestDto.getFromBankAccountId(), requestDto.getToBankAccountId(), requestDto.getAmount());

        Transaction baseTransaction = getOwnedTransferTransaction(transactionId, userId);
        String transferGroupKey = getRequiredTransferGroupKey(baseTransaction);

        List<Transaction> transferTransactions = transactionRepository.findAllByTransferGroupKeyAndUserId(transferGroupKey, userId);

        validateTransferPair(transferTransactions);

        Transaction withdrawTransaction = findWithdrawTransaction(transferTransactions);
        Transaction depositTransaction = findDepositTransaction(transferTransactions);

        rollbackTransferBalances(withdrawTransaction, depositTransaction);

        BankAccount newFromAccount = getOwnedBankAccount(requestDto.getFromBankAccountId(), userId);
        BankAccount newToAccount = getOwnedBankAccount(requestDto.getToBankAccountId(), userId);

        validateDifferentAccounts(newFromAccount.getId(), newToAccount.getId());
        validateSufficientBalance(newFromAccount, requestDto.getAmount());

        Category transferCategory = getTransferCategory(userId);
        String memo = requestDto.getMemo() == null ? "" : requestDto.getMemo().trim();

        withdrawTransaction.update(
                TransactionType.EXPENSE,
                ExpenseType.VARIABLE,
                requestDto.getAmount(),
                transferCategory,
                memo,
                requestDto.getDate(),
                newFromAccount
        );

        depositTransaction.update(
                TransactionType.INCOME,
                null,
                requestDto.getAmount(),
                transferCategory,
                memo,
                requestDto.getDate(),
                newToAccount
        );

        newFromAccount.decreaseBalance(requestDto.getAmount());
        newToAccount.increaseBalance(requestDto.getAmount());
    }

    private void rollbackTransferBalances(Transaction withdrawTransaction, Transaction depositTransaction) {
        withdrawTransaction.getBankAccount().increaseBalance(withdrawTransaction.getAmount());
        depositTransaction.getBankAccount().decreaseBalance(depositTransaction.getAmount());
    }

    private Transaction findDepositTransaction(List<Transaction> transferTransactions) {
        return transferTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));
    }

    private Transaction findWithdrawTransaction(List<Transaction> transferTransactions) {
        return transferTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));
    }

    private void validateTransferPair(List<Transaction> transferTransactions) {
        if(transferTransactions.size() != 2) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
    }

    private String getRequiredTransferGroupKey(Transaction transaction) {
        if(transaction.getTransferGroupKey() == null || transaction.getTransferGroupKey().isBlank()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        return transaction.getTransferGroupKey();
    }

    private Transaction getOwnedTransferTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND));

        if(!transaction.isTransfer()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        return transaction;
    }

    private void validateSufficientBalance(BankAccount fromAccount, Long amount) {
        if(fromAccount.getBalance() < amount) {
            throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE);
        }
    }

    private void validateDifferentAccounts(Long fromBankAccountId, Long toBankAccountId) {
        if(fromBankAccountId.equals(toBankAccountId)) {
            throw new CustomException(ErrorCode.SAME_ACCOUNT_ID);
        }
    }

    private Transaction createDepositTransaction(TransferCreateRequestDto requestDto, User user, Category transferCategory, BankAccount toAccount, String memo, String transferGroupKey) {
        return Transaction.builder()
                .type(TransactionType.INCOME)
                .expenseType(null)
                .amount(requestDto.getAmount())
                .memo(memo)
                .date(requestDto.getDate())
                .category(transferCategory)
                .user(user)
                .recurringTransaction(null)
                .bankAccount(toAccount)
                .transfer(true)
                .transferGroupKey(transferGroupKey)
                .build();
    }

    private Transaction createWithdrawTransaction(TransferCreateRequestDto requestDto, User user, Category transferCategory, BankAccount fromAccount, String memo, String transferGroupKey) {
        return Transaction.builder()
                .type(TransactionType.EXPENSE)
                .expenseType(ExpenseType.VARIABLE)
                .amount(requestDto.getAmount())
                .memo(memo)
                .date(requestDto.getDate())
                .category(transferCategory)
                .user(user)
                .recurringTransaction(null)
                .bankAccount(fromAccount)
                .transfer(true)
                .transferGroupKey(transferGroupKey)
                .build();
    }

    private BankAccount getOwnedBankAccount(Long bankAccountId, Long userId) {
        return bankAccountRepository.findByIdAndUserId(bankAccountId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    private Category getTransferCategory(Long userId) {
        return categoryRepository.findByUserIdAndName(userId, "이체")
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private void validateTransferRequest(LocalDate date, Long fromBankAccountId, Long toBankAccountId, Long amount) {
        if(date == null) {
            throw new CustomException(ErrorCode.INSERT_DATE);
        }

        if(fromBankAccountId == null || toBankAccountId == null) {
            throw new CustomException(ErrorCode.CHOOSE_ACCOUNT);
        }

        if(amount == null || amount <= 0) {
            throw new CustomException(ErrorCode.MINIMUM_AMOUNT);
        }
    }
}
