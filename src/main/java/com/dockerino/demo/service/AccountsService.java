package com.dockerino.demo.service;

import com.dockerino.demo.model.User;
import com.dockerino.demo.model.dtos.UserInfo;
import com.dockerino.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {

    private final UserRepository userRepository;

    public AccountsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserInfo getUserInfo(String email) {
        User user = userRepository.findByEmail(email);

        //TODO: setup permissions so that other users can't directly see info about other users, create other endpoint for public info

        return new UserInfo(user.id(), user.email(), user.username());
    }

}
