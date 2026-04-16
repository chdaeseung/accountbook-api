package chdaeseung.accountbook.transfer.repository;

import chdaeseung.accountbook.transfer.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
