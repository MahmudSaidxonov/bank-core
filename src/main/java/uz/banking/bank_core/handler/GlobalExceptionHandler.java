package uz.banking.bank_core.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uz.banking.bank_core.dto.ApiResponseDto;
import uz.banking.bank_core.exception.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponseDto<Void> handleValidationExceptions(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        return ApiResponseDto.<Void>builder()
                .code(400)
                .success(false)
                .message(errorMessage)
                .data(null)
                .build();
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponseDto<Void> handleUserExists(UserAlreadyExistsException e) {
        return ApiResponseDto.<Void>builder()
                .code(400)
                .success(false)
                .message(e.getMessage())
                .data(null)
                .build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseDto<Void> handleUserNotFound(UserNotFoundException e) {
        return ApiResponseDto.<Void>builder()
                .code(404)
                .success(false)
                .message(e.getMessage())
                .data(null)
                .build();
    }

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseDto<Void> handleAccountNotFound(AccountNotFoundException e) {
        return ApiResponseDto.<Void>builder()
                .code(404)
                .success(false)
                .message(e.getMessage())
                .data(null)
                .build();
    }

    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponseDto<Void> handleInsufficientFundsException(InsufficientFundsException e) {
        return ApiResponseDto.<Void>builder()
                .code(400)
                .success(false)
                .message(e.getMessage())
                .data(null)
                .build();
    }

    @ExceptionHandler(SelfTransferException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponseDto<Void> handleSelfTransferException(SelfTransferException e) {
        return ApiResponseDto.<Void>builder()
                .code(400)
                .success(false)
                .message(e.getMessage())
                .data(null)
                .build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponseDto<Void> handleIncorrectPasswordException(BadCredentialsException e) {
        return ApiResponseDto.<Void>builder()
                .code(401)
                .success(false)
                .message(e.getMessage())
                .data(null)
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponseDto<Void> handleAccessDeniedException(AccessDeniedException e) {
        return ApiResponseDto.<Void>builder()
                .code(403)
                .success(false)
                .message(e.getMessage())
                .data(null)
                .build();
    }
}
