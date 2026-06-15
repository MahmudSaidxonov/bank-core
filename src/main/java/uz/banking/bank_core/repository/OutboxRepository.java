package uz.banking.bank_core.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import uz.banking.bank_core.entity.OutboxMessage;
import uz.banking.bank_core.enums.OutboxStatus;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {
    List<OutboxMessage> findByStatus(OutboxStatus status);

    @Query(value = "SELECT * FROM outbox_messages WHERE status = 'NEW' LIMIT 50 FOR UPDATE", nativeQuery = true)
    List<OutboxMessage> findAndLockNewMessages();
}
