package uz.banking.bank_core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.banking.bank_core.dto.TransactionResponseDto;
import uz.banking.bank_core.entity.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(source = "fromAccount.id", target = "fromAccountId")
    @Mapping(source = "toAccount.id", target = "toAccountId")
    TransactionResponseDto toDto(Transaction transaction);
}
