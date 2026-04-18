package uz.banking.bank_core.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponseDto<T> {
    private int code;
    private String message;
    private boolean success;
    private T data;
}