package chdaeseung.accountbook.transfer.service;

import chdaeseung.accountbook.bank.entity.BankAccount;
import chdaeseung.accountbook.bank.repository.BankAccountRepository;
import chdaeseung.accountbook.global.exception.CustomException;
import chdaeseung.accountbook.global.exception.ErrorCode;
import chdaeseung.accountbook.transaction.entity.Transaction;
import chdaeseung.accountbook.transaction.entity.TransactionType;
import chdaeseung.accountbook.transaction.repository.TransactionRepository;
import chdaeseung.accountbook.transfer.dto.TransferRequestDto;
import chdaeseung.accountbook.user.entity.User;
import chdaeseung.accountbook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TransferService {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    public void createTransfer(Long userId, TransferRequestDto requestDto) {
        validateRequest(requestDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        BankAccount fromAccount = bankAccountRepository.findByIdAndUserId(requestDto.getFromAccountId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        BankAccount toAccount = bankAccountRepository.findByIdAndUserId(requestDto.getToAccountId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        if(fromAccount.getId().equals(toAccount.getId())) {
            throw new CustomException(ErrorCode.SAME_ACCOUNT_ID);
        }

        String memo = requestDto.getMemo() == null ? "" : requestDto.getMemo().trim();
        String transferGroupKey = UUID.randomUUID().toString();

        fromAccount.decreaseBalance(requestDto.getAmount());
        toAccount.increaseBalance(requestDto.getAmount());

        Transaction withdrawTransaction = Transaction.builder()
                .type(TransactionType.EXPENSE)
                .expenseType(null)
                .amount(requestDto.getAmount())
                .category(null)
                .memo(buildWithdrawMemo(toAccount.getAccountName(), memo))
                .date(requestDto.getDate())
                .user(user)
                .recurringTransaction(null)
                .bankAccount(fromAccount)
                .transfer(true)
                .transferGroupKey(transferGroupKey)
                .build();

        Transaction depositTransaction = Transaction.builder()
                .type(TransactionType.INCOME)
                .expenseType(null)
                .amount(requestDto.getAmount())
                .category(null)
                .memo(buildDepositMemo(fromAccount.getAccountName(), memo))
                .date(requestDto.getDate())
                .user(user)
                .recurringTransaction(null)
                .bankAccount(toAccount)
                .transfer(true)
                .transferGroupKey(transferGroupKey)
                .build();

        transactionRepository.save(withdrawTransaction);
        transactionRepository.save(depositTransaction);
    }

    private String buildWithdrawMemo(String toAccountName, String memo) {
        if(memo.isBlank()) {
            return toAccountName + "로 이체";
        }
        return memo;
    }

    private String buildDepositMemo(String fromAccountName, String memo) {
        if(memo.isBlank()) {
            return fromAccountName + "에서 이체";
        }
        return memo;
    }

    private void validateRequest(TransferRequestDto requestDto) {
        if(requestDto.getDate() == null) {
            throw new CustomException(ErrorCode.INSERT_DATE);
        }

        if(requestDto.getFromAccountId() == null || requestDto.getToAccountId() == null) {
            throw new CustomException(ErrorCode.CHOOSE_ACCOUNT);
        }

        if(requestDto.getAmount() == null || requestDto.getAmount() <= 0) {
            throw new CustomException(ErrorCode.MINIMUM_AMOUNT);
        }
    }
}
