package chdaeseung.accountbook.recurring.repository;

import chdaeseung.accountbook.recurring.entity.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long>, RecurringTransactionRepositoryCustom {
    List<RecurringTransaction> findAllByUserIdOrderByDayOfMonthAsc(Long userId);

    Optional<RecurringTransaction> findByIdAndUserId(Long id, Long userId);

    List<RecurringTransaction> findAllByDayOfMonth(Integer dayOfMonth);

    List<RecurringTransaction> findAllByUserId(Long userId);
}
