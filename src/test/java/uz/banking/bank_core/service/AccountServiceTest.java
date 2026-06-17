package uz.banking.bank_core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.banking.bank_core.dto.AccountResponseDto;
import uz.banking.bank_core.dto.DepositRequestDto;
import uz.banking.bank_core.entity.Account;
import uz.banking.bank_core.entity.User;
import uz.banking.bank_core.exception.AccessDeniedException;
import uz.banking.bank_core.exception.AccountNotFoundException;
import uz.banking.bank_core.mapper.AccountMapper;
import uz.banking.bank_core.repository.AccountRepository;
import uz.banking.bank_core.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountMapper accountMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test_user");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void depositShouldThrowAccountNotFoundException_WhenAccountDoesNotExist() {

        DepositRequestDto requestDto = new DepositRequestDto();
        requestDto.setAccountId(1L);

        when(accountRepository.findByIdWithLock(requestDto.getAccountId())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.deposit(requestDto));

        verify(accountRepository, times(1)).findByIdWithLock(requestDto.getAccountId());
        verify(accountRepository, never()).save(any());

    }

    @Test
    void depositShouldThrowAccessDeniedException_WhenAccountBelongsToAnotherUser() {

        User user = new User();
        user.setUsername("other_user");

        Account account = new Account();
        account.setId(1L);
        account.setUser(user);

        DepositRequestDto requestDto = new DepositRequestDto();
        requestDto.setAccountId(1L);

        when(accountRepository.findByIdWithLock(requestDto.getAccountId())).thenReturn(Optional.of(account));

        assertThrows(AccessDeniedException.class, () -> accountService.deposit(requestDto));

        verify(accountRepository, times(1)).findByIdWithLock(account.getId());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void depositShouldDepositSuccessfully_WhenEverythingIsOk() {
        User user = new User();
        user.setUsername("test_user");

        Account account = new Account();
        account.setId(1L);
        account.setUser(user);
        account.setBalance(new BigDecimal("1000.00"));

        DepositRequestDto requestDto = new DepositRequestDto();
        requestDto.setAccountId(1L);
        requestDto.setAmount(new BigDecimal("500.00"));

        when(accountRepository.findByIdWithLock(requestDto.getAccountId())).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountResponseDto accountResponseDto = new AccountResponseDto();
        accountResponseDto.setId(1L);
        accountResponseDto.setCurrency("UZS");
        accountResponseDto.setAccountNumber("NUMBER-XXX");
        accountResponseDto.setBalance(new BigDecimal("1500.00"));

        when(accountMapper.toDto(any(Account.class))).thenReturn(accountResponseDto);

        AccountResponseDto result = accountService.deposit(requestDto);

        assertNotNull(result);
        assertEquals(new BigDecimal("1500.00"), account.getBalance());
        assertEquals(new BigDecimal("1500.00"), result.getBalance());

        verify(accountRepository, times(1)).findByIdWithLock(requestDto.getAccountId());
        verify(accountRepository, times(1)).save(any(Account.class));

    }

//    @Test
//    void depositShouldThrowInsufficientFundsException_WhenBalanceIsLessThanZero() {
//
//        DepositRequestDto requestDto = new DepositRequestDto();
//        requestDto.setAccountId(1L);
//        requestDto.setAmount(new BigDecimal(-99));
//
//
//
//    }
}
