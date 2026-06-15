package uz.banking.bank_core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.banking.bank_core.config.RabbitMQConfig;
import uz.banking.bank_core.dto.TransactionResponseDto;
import uz.banking.bank_core.dto.TransferRequestDto;
import uz.banking.bank_core.entity.Account;
import uz.banking.bank_core.entity.OutboxMessage;
import uz.banking.bank_core.entity.Transaction;
import uz.banking.bank_core.entity.User;
import uz.banking.bank_core.enums.OutboxStatus;
import uz.banking.bank_core.enums.Role;
import uz.banking.bank_core.enums.TransactionStatus;
import uz.banking.bank_core.exception.*;
import uz.banking.bank_core.mapper.TransactionMapper;
import uz.banking.bank_core.repository.AccountRepository;
import uz.banking.bank_core.repository.OutboxRepository;
import uz.banking.bank_core.repository.TransactionRepository;
import uz.banking.bank_core.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionMapper transactionMapper;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionAuditService transactionAuditService;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public TransactionResponseDto transfer(TransferRequestDto requestDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (requestDto.getFromAccountId().equals(requestDto.getToAccountId())) {
            throw new SelfTransferException("Cannot transfer funds to the same account");
        }

        Account fromAccount;
        Account toAccount;

        if (requestDto.getFromAccountId() < requestDto.getToAccountId()) {
            fromAccount = getAccountWithLock(requestDto.getFromAccountId());
            toAccount = getAccountWithLock(requestDto.getToAccountId());
        } else {
            toAccount = getAccountWithLock(requestDto.getToAccountId());
            fromAccount = getAccountWithLock(requestDto.getFromAccountId());
        }

        if (!fromAccount.getUser().getUsername().equals(username)) {
            transactionAuditService.saveFailedTransaction(fromAccount, toAccount, requestDto.getAmount());
            throw new AccessDeniedException("Access denied! This is not your account!");
        }

        if (fromAccount.getBalance().compareTo(requestDto.getAmount()) < 0) {
            transactionAuditService.saveFailedTransaction(fromAccount, toAccount, requestDto.getAmount());
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

        TransactionResponseDto responseDto = transactionMapper.toDto(savedTransaction);

        String jsonPayload;
        try {
            jsonPayload = objectMapper.writeValueAsString(responseDto);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Unexpected server error. Transfer failed, your funds are safe. Please try again in 5 minutes.");
        }

        OutboxMessage outboxMessage = new OutboxMessage();
        outboxMessage.setEventType("EMAIL_NOTIFICATION");
        outboxMessage.setPayload(jsonPayload);
        outboxMessage.setStatus(OutboxStatus.NEW);

        outboxRepository.save(outboxMessage);

        return responseDto;
    }

    public Page<TransactionResponseDto> getMyAccountHistory(int page, int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Transaction> transactions = transactionRepository.findByFromAccount_User_UsernameOrToAccount_User_Username(username, username, pageable);

        return transactions.map(transactionMapper::toDto);
    }

    public Page<TransactionResponseDto> getAccountHistory(Long accountId, int page, int size) {

        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("Account with ID " + accountId + " not found");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Transaction> transactions = transactionRepository.findByFromAccount_IdOrToAccount_Id(accountId, accountId, pageable);

        return transactions.map(transactionMapper::toDto);
    }

    public Page<TransactionResponseDto> getAllTransactions(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Transaction> transactions = transactionRepository.findAll(pageable);

        return transactions.map(transactionMapper::toDto);
    }

    private Account getAccountWithLock(Long accountId) {
        return accountRepository.findByIdWithLock(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + accountId + " not found"));
    }
}
