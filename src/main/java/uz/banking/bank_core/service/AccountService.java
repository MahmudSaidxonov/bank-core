package uz.banking.bank_core.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import uz.banking.bank_core.dto.AccountResponseDto;
import uz.banking.bank_core.dto.DepositRequestDto;
import uz.banking.bank_core.entity.Account;
import uz.banking.bank_core.exception.AccountNotFoundException;
import uz.banking.bank_core.exception.UserNotFoundException;
import uz.banking.bank_core.repository.AccountRepository;
import uz.banking.bank_core.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public List<AccountResponseDto> getUserAccounts(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }

        List<Account> accounts = accountRepository.findAllByUserId(userId);

        return accounts.stream()
                .map(account -> {
                    AccountResponseDto accountResponseDto = new AccountResponseDto();
                    accountResponseDto.setId(account.getId());
                    accountResponseDto.setAccountNumber(account.getAccountNumber());
                    accountResponseDto.setBalance(account.getBalance());
                    accountResponseDto.setCurrency(account.getCurrency());
                    return accountResponseDto;
                })
                .toList();
    }

    @Transactional
    public AccountResponseDto deposit(DepositRequestDto requestDto) {

        Account account = accountRepository.findById(requestDto.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + requestDto.getAccountId() + " not found"));

        account.setBalance(account.getBalance().add(requestDto.getAmount()));
//        Account savedAccount = accountRepository.save(account);

        AccountResponseDto accountResponseDto = new AccountResponseDto();
        accountResponseDto.setId(account.getId());
        accountResponseDto.setAccountNumber(account.getAccountNumber());
        accountResponseDto.setBalance(account.getBalance());
        accountResponseDto.setCurrency(account.getCurrency());

        return accountResponseDto;
    }

}
