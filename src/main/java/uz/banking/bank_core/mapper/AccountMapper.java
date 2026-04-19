package uz.banking.bank_core.mapper;

import org.mapstruct.Mapper;
import uz.banking.bank_core.dto.AccountResponseDto;
import uz.banking.bank_core.entity.Account;
import uz.banking.bank_core.repository.AccountRepository;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountResponseDto toDto(Account account);
}
