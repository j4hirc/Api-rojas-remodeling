package com.rojas.remodeling.Api_rojas_remodeling.data;

import com.rojas.remodeling.Api_rojas_remodeling.model.Roles;
import com.rojas.remodeling.Api_rojas_remodeling.model.Users;
import com.rojas.remodeling.Api_rojas_remodeling.repository.RolesRepository;
import com.rojas.remodeling.Api_rojas_remodeling.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.Role;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Roles adminRole = rolesRepository.findByName("ROLE_ADMIN")
                .orElseGet(()->{
                    Roles newRole = new Roles();
                    newRole.setName("ROLE_ADMIN");
                    return rolesRepository.save(newRole);
                });

        Roles jefeRole = rolesRepository.findByName("ROLE_JEFE")
                .orElseGet(()->{
                    Roles newRole = new Roles();
                    newRole.setName("ROLE_JEFE");
                    return rolesRepository.save(newRole);
                });

        Roles employeeRole = rolesRepository.findByName("ROLE_EMPLOYEE")
                .orElseGet(()->{
                    Roles newRole = new Roles();
                    newRole.setName("ROLE_EMPLOYEE");
                    return rolesRepository.save(newRole);
                });


        if(usersRepository.findByEmail("admin@admin.com").isEmpty()){
            Users admin = new Users();
            admin.setDni("0998765432");
            admin.setFirstName("Administrador");
            admin.setLastName("Admin");

            admin.setEmail("admin@admin.com");
            admin.setPassword(passwordEncoder.encode("admin12345"));

            admin.setDateOfBirth(LocalDate.now());
            admin.setDateOfEntry(LocalDate.now());
            admin.setPhone("0933245642");
            admin.setStatus("Active");
            admin.setTitle("Administrador de la web");

            Set<Roles> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);

            admin.setRoles(adminRoles);
            usersRepository.save(admin);
            System.out.println("Admin User Created Successfully");
        }



    }
}
