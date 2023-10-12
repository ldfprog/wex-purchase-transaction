package org.lucasdf.repositories;

import org.lucasdf.models.WexTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<WexTransaction, Long> {

    public Optional<WexTransaction> findByDescription(String description);

    public Optional<WexTransaction> findById(Long id);
}
