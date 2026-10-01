package com.rojas.remodeling.Api_rojas_remodeling.service.mapper;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.ClienteRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.ClienteResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.model.Clientes;
import org.springframework.stereotype.Component;

@Component
public class ClientesMapper {

    public Clientes toEntity(ClienteRequestDto dto) {
        Clientes entity = new Clientes();
        updateEntity(dto, entity);
        return entity;
    }

    public void updateEntity(ClienteRequestDto dto, Clientes entity) {
        entity.setClientName(dto.getClientName().trim());
        entity.setCompanyName(optionalText(dto.getCompanyName()));
        entity.setContactName(optionalText(dto.getContactName()));
        entity.setClientPhone(optionalText(dto.getClientPhone()));
        entity.setAddress(dto.getAddress().trim());
        entity.setCodeBox(optionalText(dto.getCodeBox()));
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
    }

    public ClienteResponseDto toDto(Clientes entity) {
        ClienteResponseDto dto = new ClienteResponseDto();

        dto.setId(entity.getId());
        dto.setClientName(entity.getClientName());
        dto.setCompanyName(entity.getCompanyName());
        dto.setContactName(entity.getContactName());
        dto.setClientPhone(entity.getClientPhone());
        dto.setAddress(entity.getAddress());
        dto.setCodeBox(entity.getCodeBox());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());

        return dto;
    }

    private String optionalText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}