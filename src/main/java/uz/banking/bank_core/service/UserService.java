package uz.banking.bank_core.service;

import uz.banking.bank_core.dto.UserRegisterDto;
import uz.banking.bank_core.dto.UserResponseDto;
import uz.banking.bank_core.entity.Account;
import uz.banking.bank_core.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.banking.bank_core.exception.UserAlreadyExistsException;
import uz.banking.bank_core.repository.AccountRepository;
import uz.banking.bank_core.repository.UserRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public UserResponseDto registerUser(UserRegisterDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent() ||
            userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("A user with this name or email already exists!");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPasswordHash(userDto.getPassword() + "_hashed");

        User savedUser = userRepository.save(user);

        Account defaultAccount = new Account();
        defaultAccount.setUser(savedUser);
        defaultAccount.setBalance(BigDecimal.ZERO);
        defaultAccount.setCurrency("UZS");
        defaultAccount.setAccountNumber("ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        accountRepository.save(defaultAccount);

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(savedUser.getId());
        userResponseDto.setUsername(savedUser.getUsername());
        userResponseDto.setEmail(savedUser.getEmail());

        return userResponseDto;
    }
}
