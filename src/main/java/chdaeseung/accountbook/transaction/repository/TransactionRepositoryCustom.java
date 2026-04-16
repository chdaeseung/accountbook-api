package chdaeseung.accountbook.transaction.repository;

import chdaeseung.accountbook.bank.dto.MonthlyExpenseRawDto;
import chdaeseung.accountbook.dashboard.dto.AssetTrendPointDto;
import chdaeseung.accountbook.dashboard.dto.DailyCashFlowDto;
import chdaeseung.accountbook.transaction.dto.TransactionSearchDto;
import chdaeseung.accountbook.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionRepositoryCustom {
    Page<Transaction> searchTransactions(Long userId, TransactionSearchDto searchDto, Pageable pageable);

    List<DailyCashFlowDto> findDailyCashFlow(Long userId, int year, int month);

    List<MonthlyExpenseRawDto> getMonthlyExpenseByBankAccount(Long userId, Long bankAccountId);

    List<AssetTrendPointDto> getExpenseTopCategoriesByBankAccount(Long userId, Long bankAccountId);
}
