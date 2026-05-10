package uz.banking.bank_core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uz.banking.bank_core.entity.Account;
import uz.banking.bank_core.entity.Transaction;
import uz.banking.bank_core.enums.TransactionStatus;
import uz.banking.bank_core.repository.TransactionRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionAuditService {

    private final TransactionRepository transactionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailedTransaction(Account from, Account to, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setFromAccount(from);
        transaction.setToAccount(to);
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.FAILED);

        transactionRepository.save(transaction);
    }
}
