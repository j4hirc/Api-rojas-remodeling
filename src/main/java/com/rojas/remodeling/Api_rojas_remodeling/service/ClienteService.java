package com.rojas.remodeling.Api_rojas_remodeling.service;


import com.rojas.remodeling.Api_rojas_remodeling.dto.request.ClienteRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.ClienteResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.model.Clientes;

import java.util.List;

public interface ClienteService {

    List<ClienteResponseDto> getAllClientes();

    ClienteResponseDto getClienteById(Long id);

    List<ClienteResponseDto> getClienteByName(String clientName);

    ClienteResponseDto createCliente(ClienteRequestDto clienteRequestDto);

    ClienteResponseDto updateCliente(Long id, ClienteRequestDto clienteRequestDto);

    void deleteCliente(Long id);
}
