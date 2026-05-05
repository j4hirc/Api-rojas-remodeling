package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;

import com.rojas.remodeling.Api_rojas_remodeling.model.Roles;
import com.rojas.remodeling.Api_rojas_remodeling.model.Users;
import com.rojas.remodeling.Api_rojas_remodeling.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow( () ->
                        new UsernameNotFoundException("Usuario no encontrado con el Email: " + email ) );


        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), mapRolesToAuthorities(user.getRoles())
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Roles> roles){
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();
    }
}
