package chdaeseung.accountbook.recurring.repository;

import chdaeseung.accountbook.category.entity.QCategory;
import chdaeseung.accountbook.recurring.entity.QRecurringTransaction;
import chdaeseung.accountbook.recurring.entity.RecurringTransaction;
import chdaeseung.accountbook.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecurringTransactionRepositoryImpl implements RecurringTransactionRepositoryCustom {

    private final JPAQueryFactory queryFactory;
}
