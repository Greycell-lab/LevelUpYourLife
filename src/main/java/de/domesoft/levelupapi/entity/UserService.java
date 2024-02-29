package de.domesoft.levelupapi.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@SuppressWarnings("unused")
@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
}
