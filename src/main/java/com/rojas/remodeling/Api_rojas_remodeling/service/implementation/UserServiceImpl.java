package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.EditProfileDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.request.UserRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.UserResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.exception.ResourceNotFoundException;
import com.rojas.remodeling.Api_rojas_remodeling.model.Roles;
import com.rojas.remodeling.Api_rojas_remodeling.model.Users;
import com.rojas.remodeling.Api_rojas_remodeling.repository.RolesRepository;
import com.rojas.remodeling.Api_rojas_remodeling.repository.UsersRepository;
import com.rojas.remodeling.Api_rojas_remodeling.service.UserService;
import com.rojas.remodeling.Api_rojas_remodeling.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public List<UserResponseDto> findAll() {
        List<Users> users = usersRepository.findAll();

        return users.stream()
                .map(userMapper::entityToUserResponse)
                .toList();
    }



    @Override
    public UserResponseDto findById(Long id) {
        Users users = usersRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("The user with the ID could not be found: " + id));

        return userMapper.entityToUserResponse(users);
    }




    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        Users existingUser = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The user does not exist"));

        validateUniqueFieldsForUpdate(existingUser, userRequestDto.getDni(), userRequestDto.getEmail(), userRequestDto.getPhone());

        userMapper.updateEntityFromRequest(userRequestDto, existingUser);

        if(userRequestDto.getPassword() != null && !userRequestDto.getPassword().isEmpty()){
            existingUser.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        }

        if (userRequestDto.getRoles() != null && !userRequestDto.getRoles().isEmpty()) {
            Set<Roles> roles = userRequestDto.getRoles().stream()
                    .map(roleName -> rolesRepository.findByName(roleName)
                            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + roleName)))
                    .collect(Collectors.toSet());

            existingUser.setRoles(roles);
        }


        Users updatedUser = usersRepository.save(existingUser);

        return userMapper.entityToUserResponse(updatedUser);
    }



    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        validateUniqueFields(userRequestDto.getDni(), userRequestDto.getEmail(), userRequestDto.getPhone());

        Users user = userMapper.userRequestToEntity(userRequestDto);
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));

        Set<Roles> roles = userRequestDto.getRoles().stream()
                .map(roleName -> rolesRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(roles);

        Users savedUser = usersRepository.save(user);

        return userMapper.entityToUserResponse(savedUser);
    }





    @Override
    public UserResponseDto editProfile(Long id, EditProfileDto editProfileDto) {
        Users existingUser = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The user does not exist"));

        validateUniqueFieldsForUpdate(existingUser,editProfileDto.getDni(), editProfileDto.getEmail(), editProfileDto.getPhone());


        userMapper.updateEntityFromEditProfile(editProfileDto, existingUser);

        if(editProfileDto.getPassword() != null && !editProfileDto.getPassword().isEmpty()){
            existingUser.setPassword(passwordEncoder.encode(editProfileDto.getPassword()));
        }

        Users updatedUser = usersRepository.save(existingUser);

        return userMapper.entityToUserResponse(updatedUser);
    }




    @Override
    public List<UserResponseDto> findByName(String name) {
        List<Users> users = usersRepository.findByName(name);
        return users.stream()
                .map(userMapper::entityToUserResponse)
                .toList();
    }

    @Override
    public List<UserResponseDto> findByRoleName(String role) {
        List<Users> users = usersRepository.findUsersByRoleName(role);
        return users.stream()
                .map(userMapper::entityToUserResponse)
                .toList();
    }



    @Override
    public UserResponseDto findByDni(String dni) {
        Users users = usersRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("The user does not exist"));
        return userMapper.entityToUserResponse(users);
    }






    private void validateUniqueFields(String dni, String email, String phone){

        if(usersRepository.existsByDni(dni)){
            throw new RuntimeException("DNI already exists");
        }

        if(usersRepository.existsByEmail(email)){
            throw new RuntimeException("Email already exists");
        }

        if(usersRepository.existsByPhone(phone)){
            throw new RuntimeException("Phone already exists");
        }
    }


    private void validateUniqueFieldsForUpdate(Users existingUser, String newDni, String newEmail, String newPhone) {
        if (newDni != null && usersRepository.existsByDni(newDni) && !existingUser.getDni().equals(newDni)) {
            throw new RuntimeException("DNI already exists");
        }
        if (newEmail != null && usersRepository.existsByEmail(newEmail) && !existingUser.getEmail().equals(newEmail)) {
            throw new RuntimeException("Email already exists");
        }
        if (newPhone != null && usersRepository.existsByPhone(newPhone) && !existingUser.getPhone().equals(newPhone)) {
            throw new RuntimeException("Phone already exists");
        }
    }

}
