package chdaeseung.accountbook.transaction.repository;

import chdaeseung.accountbook.bank.dto.MonthlyExpenseRawDto;
import chdaeseung.accountbook.category.entity.QCategory;
import chdaeseung.accountbook.dashboard.dto.AssetTrendPointDto;
import chdaeseung.accountbook.dashboard.dto.DailyCashFlowDto;
import chdaeseung.accountbook.transaction.dto.TransactionSearchDto;
import chdaeseung.accountbook.transaction.entity.ExpenseType;
import chdaeseung.accountbook.transaction.entity.QTransaction;
import chdaeseung.accountbook.transaction.entity.Transaction;
import chdaeseung.accountbook.transaction.entity.TransactionType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

import static chdaeseung.accountbook.transaction.entity.QTransaction.transaction;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<DailyCashFlowDto> findDailyCashFlow(Long userId, int year, int month) {
        QTransaction transaction = QTransaction.transaction;

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        NumberExpression<Long> signedAmount = new CaseBuilder()
                .when(transaction.type.eq(TransactionType.INCOME))
                .then(transaction.amount)
                .otherwise(transaction.amount.multiply(-1));

        return queryFactory
                .select(Projections.constructor(
                        DailyCashFlowDto.class,
                        transaction.date.dayOfMonth(),
                        signedAmount.sum()
                ))
                .from(transaction)
                .where(
                        transaction.user.id.eq(userId),
                        transaction.date.goe(startDate),
                        transaction.date.loe(endDate)
                )
                .groupBy(transaction.date.dayOfMonth())
                .orderBy(transaction.date.dayOfMonth().asc())
                .fetch();
    }

    @Override
    public Page<Transaction> searchTransactions(Long userId, TransactionSearchDto searchDto, Pageable pageable) {
        QTransaction transaction = QTransaction.transaction;
        QCategory category = QCategory.category;

        List<Transaction> content = queryFactory
                .selectFrom(transaction)
                .leftJoin(transaction.category, category).fetchJoin()
                .where(
                        userIdEq(userId, transaction),
                        startDateGoe(searchDto.getStartDate(), transaction),
                        endDateLoe(searchDto.getEndDate(), transaction),
                        categoryIdEq(searchDto.getCategoryId(), category),
                        typeEq(searchDto.getType(), transaction),
                        expenseTypeEq(searchDto.getExpenseType(), transaction),
                        eqBankAccountId(searchDto.getBankAccountId()),
                        memoContains(searchDto.getMemoKeyword())
                )
                .orderBy(transaction.date.desc(), transaction.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(transaction.count())
                .from(transaction)
                .leftJoin(transaction.category, category)
                .where(
                        userIdEq(userId, transaction),
                        startDateGoe(searchDto.getStartDate(), transaction),
                        endDateLoe(searchDto.getEndDate(), transaction),
                        categoryIdEq(searchDto.getCategoryId(), category),
                        typeEq(searchDto.getType(), transaction),
                        expenseTypeEq(searchDto.getExpenseType(), transaction),
                        eqBankAccountId(searchDto.getBankAccountId()),
                        memoContains(searchDto.getMemoKeyword())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    @Override
    public List<MonthlyExpenseRawDto> getMonthlyExpenseByBankAccount(Long userId, Long bankAccountId) {
        QTransaction transaction = QTransaction.transaction;

        return queryFactory
                .select(Projections.constructor(
                        MonthlyExpenseRawDto.class,
                        transaction.date.year(),
                        transaction.date.month(),
                        transaction.amount.sum()
                ))
                .from(transaction)
                .where(
                        transaction.user.id.eq(userId),
                        transaction.bankAccount.id.eq(bankAccountId)
                )
                .groupBy(transaction.date.year(), transaction.date.month())
                .orderBy(transaction.date.year().asc(), transaction.date.month().asc())
                .fetch();
    }

    @Override
    public List<AssetTrendPointDto> getExpenseTopCategoriesByBankAccount(Long userId, Long bankAccountId) {
        QTransaction transaction = QTransaction.transaction;
        QCategory category = QCategory.category;

        return queryFactory
                .select(Projections.constructor(
                        AssetTrendPointDto.class,
                        category.name,
                        transaction.amount.sum()
                ))
                .from(transaction)
                .join(transaction.category, category)
                .where(
                        transaction.user.id.eq(userId),
                        transaction.bankAccount.id.eq(bankAccountId),
                        transaction.type.eq(TransactionType.EXPENSE)
                )
                .groupBy(category.id, category.name)
                .orderBy(transaction.amount.sum().desc())
                .limit(3)
                .fetch();
    }

    private BooleanExpression userIdEq(Long userId, QTransaction transaction) {
        return userId != null ? transaction.user.id.eq(userId) : null;
    }

    private BooleanExpression startDateGoe(LocalDate startDate, QTransaction transaction) {
        return startDate != null ? transaction.date.goe(startDate) : null;
    }

    private BooleanExpression endDateLoe(LocalDate endDate, QTransaction transaction) {
        return endDate != null ? transaction.date.loe(endDate) : null;
    }

    private BooleanExpression categoryIdEq(Long categoryId, QCategory category) {
        return categoryId != null ? category.id.eq(categoryId) : null;
    }

    private BooleanExpression typeEq(TransactionType type, QTransaction transaction) {
        return type != null ? transaction.type.eq(type) : null;
    }

    private BooleanExpression expenseTypeEq(ExpenseType expenseType, QTransaction transaction) {
        return expenseType != null ? transaction.expenseType.eq(expenseType) : null;
    }

    private BooleanExpression eqBankAccountId(Long bankAccountId) {
        return bankAccountId != null ? transaction.bankAccount.id.eq(bankAccountId) : null;
    }

    private BooleanExpression memoContains(String memoKeyword) {
        return StringUtils.hasText(memoKeyword) ? transaction.memo.contains(memoKeyword) : null;
    }
}
