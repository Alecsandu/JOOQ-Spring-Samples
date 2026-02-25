package com.dockerino.demo.service;

import com.dockerino.demo.model.AppUser;
import com.dockerino.demo.model.Role;
import com.dockerino.demo.model.User;
import com.dockerino.demo.repository.RoleRepository;
import com.dockerino.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProviderService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    public ProviderService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public AppUser getOrCreate(String sub) {

        User user = userRepository.findUserBySub(sub)
                .orElseGet(() -> {
                    Role userRole = roleRepository.findRoleByName("user");
                    return userRepository.save(sub, userRole.id());
                });

        List<Role> roles = roleRepository.findRoleNamesByUserId(user.id());

        return new AppUser(user, roles);
    }
}
