package chdaeseung.accountbook.recurring.service;

import chdaeseung.accountbook.recurring.entity.RecurringTransaction;
import chdaeseung.accountbook.recurring.repository.RecurringTransactionRepository;
import chdaeseung.accountbook.transaction.entity.ExpenseType;
import chdaeseung.accountbook.transaction.entity.Transaction;
import chdaeseung.accountbook.transaction.entity.TransactionType;
import chdaeseung.accountbook.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RecurringSchedulerService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final TransactionRepository transactionRepository;

    public void generateTodayRecurringTransactions() {
        LocalDate today = LocalDate.now();

        List<RecurringTransaction> recurringTransactions = recurringTransactionRepository.findAllByDayOfMonth(today.getDayOfMonth());

        log.info("오늘 자동 생성 대상 개수 : {}", recurringTransactions.size());

        for(RecurringTransaction recurringTransaction : recurringTransactions) {
            boolean exists = transactionRepository.existsByRecurringTransactionIdAndDate(recurringTransaction.getId(), today);

            if(exists) {
                log.info("이미 생성됨 : recurringId = {}, date = {}", recurringTransaction.getId(), today);
                continue;
            }

            Transaction transaction = Transaction.builder()
                    .recurringTransaction(recurringTransaction)
                    .bankAccount(recurringTransaction.getBankAccount())
                    .amount(recurringTransaction.getAmount())
                    .memo(recurringTransaction.getMemo())
                    .type(TransactionType.EXPENSE)
                    .expenseType(ExpenseType.FIXED)
                    .user(recurringTransaction.getUser())
                    .date(today)
                    .build();

            recurringTransaction.getBankAccount().decreaseBalance(recurringTransaction.getAmount());

            transactionRepository.save(transaction);

            log.info("자동 생성 완료 : recurringId = {}, date = {}", recurringTransaction.getId(), today);
        }
    }
}