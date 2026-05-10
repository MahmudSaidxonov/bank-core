package uz.banking.bank_core.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import uz.banking.bank_core.dto.AuthResponseDto;
import uz.banking.bank_core.dto.LoginRequestDto;
import uz.banking.bank_core.dto.UserRegisterDto;
import uz.banking.bank_core.dto.UserResponseDto;
import uz.banking.bank_core.entity.Account;
import uz.banking.bank_core.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.banking.bank_core.enums.Role;
import uz.banking.bank_core.exception.BadCredentialsException;
import uz.banking.bank_core.exception.UserAlreadyExistsException;
import uz.banking.bank_core.exception.UserNotFoundException;
import uz.banking.bank_core.mapper.UserMapper;
import uz.banking.bank_core.repository.AccountRepository;
import uz.banking.bank_core.repository.UserRepository;
import uz.banking.bank_core.security.JwtUtil;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserResponseDto registerUser(UserRegisterDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent() ||
            userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("A user with this name or email already exists!");
        }

        User user = userMapper.toEntity(userDto);
        user.setRole(Role.ROLE_USER);
        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));

        User savedUser = userRepository.save(user);

        Account defaultAccount = new Account();
        defaultAccount.setUser(savedUser);
        defaultAccount.setBalance(BigDecimal.ZERO);
        defaultAccount.setCurrency("UZS");
        defaultAccount.setAccountNumber("ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        accountRepository.save(defaultAccount);

        return userMapper.toDto(savedUser);
    }

    public AuthResponseDto login (LoginRequestDto requestDto) {

        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        AuthResponseDto responseDto = new AuthResponseDto();
        responseDto.setToken(jwtUtil.generateToken(requestDto.getUsername(), user.getRole()));
        
        return responseDto;
    }
}
