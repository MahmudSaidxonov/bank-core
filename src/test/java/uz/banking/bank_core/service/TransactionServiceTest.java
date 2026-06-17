package uz.banking.bank_core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Enumerated;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.banking.bank_core.dto.TransactionResponseDto;
import uz.banking.bank_core.dto.TransferRequestDto;
import uz.banking.bank_core.entity.Account;
import uz.banking.bank_core.entity.OutboxMessage;
import uz.banking.bank_core.entity.Transaction;
import uz.banking.bank_core.entity.User;
import uz.banking.bank_core.enums.TransactionStatus;
import uz.banking.bank_core.exception.InsufficientFundsException;
import uz.banking.bank_core.exception.SelfTransferException;
import uz.banking.bank_core.mapper.TransactionMapper;
import uz.banking.bank_core.repository.AccountRepository;
import uz.banking.bank_core.repository.OutboxRepository;
import uz.banking.bank_core.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private  AccountRepository accountRepository;
    @Mock
    private  TransactionRepository transactionRepository;
    @Mock
    private  TransactionMapper  transactionMapper;
    @Mock
    private  TransactionAuditService transactionAuditService;
    @Spy
    private ObjectMapper objectMapper;
    @Mock
    private OutboxRepository outboxRepository;
//    @Mock
//    private org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test_user");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void transferShouldThrowSelfTransferException_WhenAccountsAreSame() {
        TransferRequestDto requestDto = new TransferRequestDto();
        requestDto.setFromAccountId(1L);
        requestDto.setToAccountId(1L);
        requestDto.setAmount(new BigDecimal("5000.00"));

        assertThrows(SelfTransferException.class, () -> transactionService.transfer(requestDto));

        verifyNoInteractions(accountRepository);
    }

    @Test
    void transferShouldThrowInsufficientFundsException_WhenBalanceIsLow() {

        User fromUser = new User();
        fromUser.setUsername("test_user");

        User toUser = new User();
        toUser.setUsername("to_user");

        Account fromAccount = new Account();
        fromAccount.setId(1L);
        fromAccount.setUser(fromUser);
        fromAccount.setBalance(new BigDecimal("2000.00"));

        Account toAccount = new Account();
        toAccount.setId(2L);
        toAccount.setUser(toUser);
        toAccount.setBalance(new BigDecimal("1000.00"));

        TransferRequestDto requestDto = new TransferRequestDto();
        requestDto.setFromAccountId(1L);
        requestDto.setToAccountId(2L);
        requestDto.setAmount(new BigDecimal("5000.00"));

        when(accountRepository.findByIdWithLock(fromAccount.getId())).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdWithLock(toAccount.getId())).thenReturn(Optional.of(toAccount));

        assertThrows(InsufficientFundsException.class, () -> transactionService.transfer(requestDto));

        verify(accountRepository, times(1)).findByIdWithLock(fromAccount.getId());
        verify(accountRepository, times(1)).findByIdWithLock(toAccount.getId());
        verify(transactionAuditService, times(1)).saveFailedTransaction(fromAccount, toAccount, requestDto.getAmount());
//        verify(transactionRepository, never()).save(any());
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void transferShouldTransferFundsSuccessfully_WhenEverythingIsOk() {
        User fromUser = new User();
        fromUser.setUsername("test_user");

        User toUser = new User();
        toUser.setUsername("to_user");

        Account fromAccount = new Account();
        fromAccount.setId(1L);
        fromAccount.setUser(fromUser);
        fromAccount.setBalance(new BigDecimal("8000.00"));

        Account toAccount = new Account();
        toAccount.setId(2L);
        toAccount.setUser(toUser);
        toAccount.setBalance(new BigDecimal("1000.00"));

        TransferRequestDto requestDto = new TransferRequestDto();
        requestDto.setFromAccountId(1L);
        requestDto.setToAccountId(2L);
        requestDto.setAmount(new BigDecimal("4000.00"));

        when(accountRepository.findByIdWithLock(fromAccount.getId())).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdWithLock(toAccount.getId())).thenReturn(Optional.of(toAccount));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponseDto responseDto = new TransactionResponseDto();
        responseDto.setFromAccountId(fromAccount.getId());
        responseDto.setToAccountId(toAccount.getId());
        responseDto.setAmount(new BigDecimal("4000.00"));
        responseDto.setStatus(TransactionStatus.SUCCESS);

        when(transactionMapper.toDto(any(Transaction.class))).thenReturn(responseDto);
        when(outboxRepository.save(any(OutboxMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponseDto result = transactionService.transfer(requestDto);

        assertNotNull(result);
        assertEquals(new BigDecimal("4000.00"), result.getAmount());
        assertEquals(TransactionStatus.SUCCESS, result.getStatus());

        assertEquals(new BigDecimal("4000.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("5000.00"), toAccount.getBalance());

        verify(accountRepository, times(1)).findByIdWithLock(fromAccount.getId());
        verify(accountRepository, times(1)).findByIdWithLock(toAccount.getId());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(outboxRepository, times(1)).save(any(OutboxMessage.class));
        verify(transactionAuditService, never()).saveFailedTransaction(fromAccount, toAccount, requestDto.getAmount());
    }
}