package com.blog.javablogging.service;

import com.blog.javablogging.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends GeneralService<User>, UserDetailsService {

    User loadUserByEmail(String email);
    boolean uniqueUserAvailable(String username);
    boolean uniqueEmailAvailable(String email);
    Optional<User> findUserByResetPin(Integer pin);
}
