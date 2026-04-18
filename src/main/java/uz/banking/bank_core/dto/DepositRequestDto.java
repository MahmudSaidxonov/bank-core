package uz.banking.bank_core.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepositRequestDto {
    private Long accountId;
    @NotNull(message = "Amount is required")
    @Positive(message = "Top-up amount must be greater than zero")
    BigDecimal amount;
}
