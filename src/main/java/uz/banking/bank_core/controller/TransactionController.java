package uz.banking.bank_core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
