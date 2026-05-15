package uz.banking.bank_core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import uz.banking.bank_core.dto.ApiResponseDto;
import uz.banking.bank_core.dto.UserResponseDto;
import uz.banking.bank_core.exception.AccessDeniedException;
import uz.banking.bank_core.service.UserService;

@RestController
@RequestMapping("internal")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @Value("${internal.secret.key}")
    private String internalSecret;

    @GetMapping("users/get-by-id/{id}")
    public ApiResponseDto<UserResponseDto> getUserInfo(
            @PathVariable Long id,
            @RequestHeader(value = "X-Internal-Secret") String requestSecret) {

        if (!internalSecret.equals(requestSecret)) {
            throw new AccessDeniedException("Access denied! Invalid internal key.");
        }

        UserResponseDto responseDto = userService.getUserById(id);

        return ApiResponseDto.<UserResponseDto>builder()
                .code(200)
                .message("success")
                .success(true)
                .data(responseDto)
                .build();
    }
}
