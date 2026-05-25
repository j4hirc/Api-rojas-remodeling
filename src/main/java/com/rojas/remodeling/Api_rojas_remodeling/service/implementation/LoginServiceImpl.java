package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;

import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JwtAuthResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.request.LoginRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.exception.ResourceNotFoundException;
import com.rojas.remodeling.Api_rojas_remodeling.model.Users;
import com.rojas.remodeling.Api_rojas_remodeling.repository.UsersRepository;
import com.rojas.remodeling.Api_rojas_remodeling.security.jwt.JwtGenerator;
import com.rojas.remodeling.Api_rojas_remodeling.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final AuthenticationManager authenticationManager;
    private final JwtGenerator jwtGenerator;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public JwtAuthResponseDto authenticateUser(LoginRequestDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateToken(authentication);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtAuthResponseDto(token, loginDto.getEmail(), roles);
    }


    @Override
    @Transactional
    public void forgotPassword(String email) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ese correo."));

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);

        user.setPassword(passwordEncoder.encode(tempPassword));
        usersRepository.save(user);

        String subject = "Recuperación de Contraseña - Rojas Remodeling";
        String body = "Hola " + user.getFirstName() + ",\n\n" +
                "Se ha solicitado un restablecimiento de contraseña para tu cuenta en el sistema Rojas Remodeling.\n\n" +
                "Tu nueva contraseña provisional es: " + tempPassword + "\n\n" +
                "Te recomendamos iniciar sesión y cambiar esta contraseña por una propia lo antes posible.\n\n" +
                "Saludos,\nSistema Rojas Remodeling.";

        emailService.sendEmail(user.getEmail(), subject, body);
    }


}