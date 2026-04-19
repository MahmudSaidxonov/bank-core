package uz.banking.bank_core.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uz.banking.bank_core.dto.TransactionResponseDto;
import uz.banking.bank_core.dto.TransferRequestDto;
import uz.banking.bank_core.entity.Account;
import uz.banking.bank_core.entity.Transaction;
import uz.banking.bank_core.enums.TransactionStatus;
import uz.banking.bank_core.exception.AccountNotFoundException;
import uz.banking.bank_core.exception.InsufficientFundsException;
import uz.banking.bank_core.exception.SelfTransferException;
import uz.banking.bank_core.exception.UserNotFoundException;
import uz.banking.bank_core.mapper.TransactionMapper;
import uz.banking.bank_core.repository.AccountRepository;
import uz.banking.bank_core.repository.TransactionRepository;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionMapper transactionMapper;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionResponseDto transfer(TransferRequestDto requestDto) {

        if (requestDto.getFromAccountId().equals(requestDto.getToAccountId())) {
            throw new SelfTransferException("Cannot transfer funds to the same account");
        }

        Account fromAccount = accountRepository.findById(requestDto.getFromAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + requestDto.getFromAccountId() + " not found"));

        Account toAccount = accountRepository.findById(requestDto.getToAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + requestDto.getToAccountId() + " not found"));

        if(fromAccount.getBalance().compareTo(requestDto.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(requestDto.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(requestDto.getAmount()));

        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(requestDto.getAmount());
        transaction.setStatus(TransactionStatus.SUCCESS);

        Transaction savedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toDto(savedTransaction);
    }
}
