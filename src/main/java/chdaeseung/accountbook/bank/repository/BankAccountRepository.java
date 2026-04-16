package chdaeseung.accountbook.bank.repository;

import chdaeseung.accountbook.bank.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findAllByUserIdOrderByIdDesc(Long userId);

    Optional<BankAccount> findByIdAndUserId(Long id, Long userId);

    List<BankAccount> findTop5ByUserIdOrderByBalanceDesc(Long userId);

    List<BankAccount> findAllByUserId(Long userId);
}
