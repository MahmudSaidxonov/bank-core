package uz.banking.bank_core.mapper;

import org.mapstruct.Mapper;
import uz.banking.bank_core.dto.UserRegisterDto;
import uz.banking.bank_core.dto.UserResponseDto;
import uz.banking.bank_core.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toEntity(UserRegisterDto userRegisterDto);
}
