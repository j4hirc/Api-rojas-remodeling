package com.rojas.remodeling.Api_rojas_remodeling.controller;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.ClienteRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.ClienteResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clientes")
public class ClientesController {

    private final ClienteService clientesService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE', 'EMPLOYEE', 'BODEGUERO')")
    public ResponseEntity<List<ClienteResponseDto>> getAllClientes() {
        return ResponseEntity.ok(clientesService.getAllClientes());
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE', 'EMPLOYEE', 'BODEGUERO')")
    public ResponseEntity<ClienteResponseDto> getClienteById(@PathVariable Long id) {
        return ResponseEntity.ok(clientesService.getClienteById(id));
    }

    @GetMapping("/name/{clientName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE', 'EMPLOYEE', 'BODEGUERO')")
    public ResponseEntity<List<ClienteResponseDto>> getClienteByName(@PathVariable String clientName) {
        return ResponseEntity.ok(clientesService.getClienteByName(clientName));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE', 'EMPLOYEE', 'BODEGUERO')")
    public ResponseEntity<ClienteResponseDto> createCliente(@Valid @RequestBody ClienteRequestDto clienteRequestDto) {
        ClienteResponseDto clienteResponseDto = clientesService.createCliente(clienteRequestDto);
        return new ResponseEntity<>(clienteResponseDto, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE', 'EMPLOYEE', 'BODEGUERO')")
    public ResponseEntity<ClienteResponseDto> updateCliente(@PathVariable Long id, @RequestBody ClienteRequestDto clienteRequestDto) {
        return ResponseEntity.ok(clientesService.updateCliente(id, clienteRequestDto));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE', 'EMPLOYEE', 'BODEGUERO')")
    public ResponseEntity<?> deleteCliente(@PathVariable Long id) {
        clientesService.deleteCliente(id);
        return ResponseEntity.ok("Cliente eliminado correctamente");
    }

}
