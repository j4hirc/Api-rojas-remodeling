package com.rojas.remodeling.Api_rojas_remodeling.service.mapper;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.ClienteRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.ClienteResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.model.Clientes;
import org.springframework.stereotype.Component;

@Component
public class ClientesMapper {

    public Clientes toEntity(ClienteRequestDto dto) {
        Clientes entity = new Clientes();
        entity.setClientName(dto.getClientName());
        entity.setClientPhone(dto.getClientPhone());
        entity.setAddress(dto.getAddress());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        return entity;
    }

    public ClienteResponseDto toDto(Clientes entity) {
        ClienteResponseDto dto = new ClienteResponseDto();
        dto.setId(entity.getId());
        dto.setClientName(entity.getClientName());
        dto.setClientPhone(entity.getClientPhone());
        dto.setAddress(entity.getAddress());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        return dto;
    }

}
