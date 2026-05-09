package uz.banking.bank_core.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import uz.banking.bank_core.dto.ApiResponseDto;
import uz.banking.bank_core.dto.TransactionResponseDto;
import uz.banking.bank_core.dto.TransferRequestDto;
import uz.banking.bank_core.service.TransactionService;

@RestController()
@RequestMapping("transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("transfer")
    public ApiResponseDto<TransactionResponseDto> transfer(@Valid @RequestBody TransferRequestDto requestDto) {
        TransactionResponseDto responseDto = transactionService.transfer(requestDto);

        return ApiResponseDto.<TransactionResponseDto>builder()
                .code(200)
                .message("success")
                .success(true)
                .data(responseDto)
                .build();
    }

    @GetMapping("history/{accountId}")
    public ApiResponseDto<Page<TransactionResponseDto>> getTransactionHistory(
        @PathVariable Long accountId,
        @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page must be at least 0") int page,
        @RequestParam(defaultValue = "10") @Max(value = 100, message = "Max page size: 100") int size)
    {
        Page<TransactionResponseDto> history = transactionService.getAccountHistory(accountId, page, size);

        return ApiResponseDto.<Page<TransactionResponseDto>>builder()
                .code(200)
                .message("success")
                .success(true)
                .data(history)
                .build();
    }

    @GetMapping("all")
    public ApiResponseDto<Page<TransactionResponseDto>> getAllTransactions(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page must be at least 0") int page,
            @RequestParam(defaultValue = "10") @Max(value = 100, message = "Max page size: 100") int size)
    {
        Page<TransactionResponseDto> history = transactionService.getAllTransactions(page, size);

        return ApiResponseDto.<Page<TransactionResponseDto>>builder()
                .code(200)
                .message("success")
                .success(true)
                .data(history)
                .build();
    }

}
