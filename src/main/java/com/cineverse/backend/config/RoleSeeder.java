package com.cineverse.backend.config;

import com.cineverse.backend.entity.Role;
import com.cineverse.backend.repository.RoleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleSeeder implements ApplicationRunner {

    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedIfMissing("ROLE_CUSTOMER", "Default role for registered customers");
        seedIfMissing("ROLE_THEATRE_OWNER", "Theatre partner account, manages theatres/screens/shows");
        seedIfMissing("ROLE_ADMIN", "Platform administrator");
    }

    private void seedIfMissing(String name, String description) {
        roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(Role.builder().name(name).description(description).build()));
    }
}
