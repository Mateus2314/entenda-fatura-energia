package com.understand_your_electricity_bill.mapper;

import com.understand_your_electricity_bill.dto.UserDTO;
import com.understand_your_electricity_bill.model.User;
import com.understand_your_electricity_bill.model.enums.UserStatus;
import com.understand_your_electricity_bill.model.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Test
    @DisplayName("Should map User entity to UserDTO")
    void shouldMapUserToUserDto() {
        // Given
        User userEntity = new User();
        userEntity.setId(UUID.randomUUID());
        userEntity.setName("Test User");
        userEntity.setEmail("test@example.com");
        userEntity.setPasswordHash("should-not-be-mapped");
        userEntity.setPhone("11999998888");
        userEntity.setUserType(UserType.CLIENT);
        userEntity.setStatus(UserStatus.ACTIVE);

        // When
        UserDTO userDTO = userMapper.toDTO(userEntity);

        // Then
        assertThat(userDTO).isNotNull();
        assertThat(userDTO.id()).isEqualTo(userEntity.getId());
        assertThat(userDTO.name()).isEqualTo(userEntity.getName());
        assertThat(userDTO.email()).isEqualTo(userEntity.getEmail());
        assertThat(userDTO.phone()).isEqualTo(userEntity.getPhone());
        assertThat(userDTO.userType()).isEqualTo(userEntity.getUserType());
        assertThat(userDTO.status()).isEqualTo(userEntity.getStatus());
    }

}
