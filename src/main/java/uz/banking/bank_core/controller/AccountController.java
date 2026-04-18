package uz.banking.bank_core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.banking.bank_core.dto.AccountResponseDto;
import uz.banking.bank_core.dto.ApiResponseDto;
import uz.banking.bank_core.dto.DepositRequestDto;
import uz.banking.bank_core.service.AccountService;

import java.util.List;

@RestController
@RequestMapping("accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("user/{userId}")
    public ApiResponseDto<List<AccountResponseDto>> getAccounts (@PathVariable Long userId) {
        List<AccountResponseDto> accountResponseDto = accountService.getUserAccounts(userId);
        return ApiResponseDto.<List<AccountResponseDto>>builder()
                .code(200)
                .message("success")
                .success(true)
                .data(accountResponseDto)
                .build();
    }

    @PostMapping("deposit")
    public ApiResponseDto<AccountResponseDto> deposit (@Valid @RequestBody DepositRequestDto requestDto) {
        AccountResponseDto responseDto = accountService.deposit(requestDto);
        return ApiResponseDto.<AccountResponseDto>builder()
                .code(200)
                .message("success")
                .success(true)
                .data(responseDto)
                .build();
    }
}
