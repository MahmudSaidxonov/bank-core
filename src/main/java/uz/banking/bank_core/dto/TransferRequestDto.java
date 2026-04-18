package uz.banking.bank_core.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.banking.bank_core.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequestDto {
    private Long fromAccountId;
    private Long toAccountId;
    @NotNull(message = "Amount is required")
    @Positive(message = "Top-up amount must be greater than zero")
    private BigDecimal amount;
}
