package uz.banking.bank_core.controller;

import uz.banking.bank_core.dto.ApiResponseDto;
import uz.banking.bank_core.dto.UserRegisterDto;
import uz.banking.bank_core.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.banking.bank_core.service.UserService;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("register")
    public ApiResponseDto<UserResponseDto> registerUser(@RequestBody UserRegisterDto userRegisterDto) {
        UserResponseDto userResponseDto = userService.registerUser(userRegisterDto);
        return ApiResponseDto.<UserResponseDto>builder()
                .code(200)
                .message("success")
                .success(true)
                .data(userResponseDto)
                .build();
    }
}
