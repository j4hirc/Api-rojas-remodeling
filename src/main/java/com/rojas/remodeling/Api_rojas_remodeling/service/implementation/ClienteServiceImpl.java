package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.ClienteRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.ClienteResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.exception.ResourceNotFoundException;
import com.rojas.remodeling.Api_rojas_remodeling.model.Clientes;
import com.rojas.remodeling.Api_rojas_remodeling.repository.ClientesRepository;
import com.rojas.remodeling.Api_rojas_remodeling.service.ClienteService;
import com.rojas.remodeling.Api_rojas_remodeling.service.mapper.ClientesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClientesRepository clientesRepository;
    private final ClientesMapper clientesMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDto> getAllClientes() {
        return clientesRepository.findAll().stream()
                .map(clientesMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDto getClienteById(Long id) {
        Clientes cliente = clientesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente not found with ID: " + id));

        return clientesMapper.toDto(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDto> getClienteByName(String clientName) {
        List<Clientes> clientes = clientesRepository.findByClientName(clientName);

        if (clientes.isEmpty()) {
            throw new ResourceNotFoundException("No clients found with name: " + clientName);
        }

        return clientes.stream()
                .map(clientesMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ClienteResponseDto createCliente(ClienteRequestDto clienteRequestDto) {
        Clientes clientes = clientesMapper.toEntity(clienteRequestDto);

        Clientes savedCliente = clientesRepository.save(clientes);
        return clientesMapper.toDto(savedCliente);
    }

    @Override
    @Transactional
    public ClienteResponseDto updateCliente(
            Long id,
            ClienteRequestDto clienteRequestDto
    ) {
        Clientes cliente = clientesRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cliente not found with ID: " + id
                        )
                );

        clientesMapper.updateEntity(clienteRequestDto, cliente);

        Clientes updatedCliente = clientesRepository.save(cliente);
        return clientesMapper.toDto(updatedCliente);
    }

    @Override
    @Transactional
    public void deleteCliente(Long id) {
        Clientes clientes = clientesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente not found with ID: " + id));

        clientesRepository.delete(clientes);

    }
}
