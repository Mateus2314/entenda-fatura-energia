package com.understand_your_electricity_bill.mapper;

import com.understand_your_electricity_bill.dto.ClientDTO;
import com.understand_your_electricity_bill.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper( componentModel = "spring")
public interface ClientMapper {

    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    /**
     * Maps a Client entity to a ClientDTO.
     *
     * @param client The Client entity to map.
     * @return The mapped ClientDTO.
     */
    ClientDTO toDTO(Client client);

    /**
     * Maps a ClientDTO to a Client entity.
     *
     * @param clientDTO The ClientDTO to map.
     * @return The mapped Client entity.
     */
    // @Mapping(target = "bills", ignore = true) // Ignora coleções para evitar problemas de mapeamento
    @Mapping(target = "userType", ignore = true) // Ignora o tipo de usuário, pois é definido na criação
    @Mapping(target = "passwordHash", ignore = true) // Nunca mapeie senhas do DTO para a entidade
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Client toEntity(ClientDTO clientDTO);

}
