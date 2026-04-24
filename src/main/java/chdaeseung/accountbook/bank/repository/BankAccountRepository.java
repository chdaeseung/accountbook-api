package chdaeseung.accountbook.bank.repository;

import chdaeseung.accountbook.bank.entity.BankAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findAllByUserIdOrderByIdDesc(Long userId);

    Optional<BankAccount> findByIdAndUserId(Long id, Long userId);

    List<BankAccount> findTop5ByUserIdOrderByBalanceDesc(Long userId);

    List<BankAccount> findAllByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from BankAccount b where b.id = :id and b.user.id = :userId")
    Optional<BankAccount> findByIdAndUserIdForUpdate(@Param("id") Long id, @Param("userId") Long userId);
}
