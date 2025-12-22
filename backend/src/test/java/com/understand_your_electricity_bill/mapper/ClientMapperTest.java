package com.understand_your_electricity_bill.mapper;

import com.understand_your_electricity_bill.dto.ClientDTO;
import com.understand_your_electricity_bill.model.Client;
import com.understand_your_electricity_bill.model.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ClientMapperTest {

    private final ClientMapper clientMapper = ClientMapper.INSTANCE;

    @Test
    @DisplayName("Should map Client entity to ClientDTO")
    void shouldMapClientToClientDto() {
        // Given
        Client client = new Client();
        client.setId(UUID.randomUUID());
        client.setName("Test Client");
        client.setEmail("client@example.com");
        client.setPhone("11987654321");
        client.setStatus(UserStatus.ACTIVE);
        client.setAddress("123 Main St");
        client.setCity("Anytown");
        client.setState("SP");
        client.setZipCode("12345678");
        client.setCpf("11122233344");
        client.setRegistrationDate(LocalDate.now());

        // When
        ClientDTO mappedDto = clientMapper.toDTO(client);

        // Then
        assertThat(mappedDto).isNotNull();
        assertThat(mappedDto.id()).isEqualTo(client.getId());
        assertThat(mappedDto.name()).isEqualTo(client.getName());
        assertThat(mappedDto.email()).isEqualTo(client.getEmail());
        assertThat(mappedDto.cpf()).isEqualTo(client.getCpf());
        assertThat(mappedDto.address()).isEqualTo(client.getAddress());
        assertThat(mappedDto.status()).isEqualTo(client.getStatus());
    }

    @Test
    @DisplayName("Should map ClientDTO to Client entity")
    void shouldMapClientDtoToClient() {
        // Given
        ClientDTO clientDTO = new ClientDTO(
                UUID.randomUUID(),
                "dto@example.com",
                "DTO Client",
                "21999998888",
                UserStatus.ACTIVE,
                "456 Other St",
                "Otherville",
                "RJ",
                "87654321",
                "55566677788",
                LocalDate.now()
        );

        // When
        Client mappedEntity = clientMapper.toEntity(clientDTO);

        // Then
        assertThat(mappedEntity).isNotNull();
        assertThat(mappedEntity.getId()).isEqualTo(clientDTO.id());
        assertThat(mappedEntity.getName()).isEqualTo(clientDTO.name());
        assertThat(mappedEntity.getEmail()).isEqualTo(clientDTO.email());
        assertThat(mappedEntity.getCpf()).isEqualTo(clientDTO.cpf());

        // Campos ignorados no mapeamento devem ser nulos
        assertThat(mappedEntity.getPasswordHash()).isNull();

    }
}
