package com.understand_your_electricity_bill.mapper;

import com.understand_your_electricity_bill.dto.UserDTO;
import com.understand_your_electricity_bill.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDTO(User user);

}
