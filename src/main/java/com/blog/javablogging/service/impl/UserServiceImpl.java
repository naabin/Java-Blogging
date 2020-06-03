package com.blog.javablogging.service.impl;

import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.blog.javablogging.config.securityconfig.PasswordEncrypt;
import com.blog.javablogging.model.User;
import com.blog.javablogging.model.security.oauth.UserPrincipal;
import com.blog.javablogging.repository.UserRepository;
import com.blog.javablogging.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncrypt passwordEncrypt;

    public UserServiceImpl(UserRepository userRepository, PasswordEncrypt passwordEncrypt) {
        this.userRepository = userRepository;
        this.passwordEncrypt = passwordEncrypt;
    }

    @Override
    public User loadUserByEmail(String email) {
        User user = this.userRepository.findByEmail(email);
        if (user == null){
            LOG.warn( email  + " could not be found");
            throw new UserNotFoundException(email + " not found");
        } else {
            return user;
        }
    }

    @Override
    public boolean uniqueUserAvailable(String username) {
        User user = this.userRepository.findUserByName(username).get();
        return !user.getName().equalsIgnoreCase(username);
    }

    @Override
    public boolean uniqueEmailAvailable(String email) {
        User byEmail = this.userRepository.findByEmail(email);
        return !(byEmail != null && byEmail.getEmail().equalsIgnoreCase(email));

    }

    @Override
    public Optional<User> findUserByResetPin(Integer pin) {
        return this.userRepository.findUserByResetPin(pin);
    }

    @Override
    public User create(User user) {
        User newUser = this.userRepository.findByEmail(user.getEmail());
        if (newUser != null){
            LOG.info("User " + user.getName() + " already exists");
        } else {
            user.setPassword(this.passwordEncrypt.passwordEncoder().encode(user.getPassword()));
            return userRepository.save(user);
        }
        return newUser;
    }

    //TODO: to be implemented later
    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public Optional<User> getById(Integer id) {
        return this.userRepository.findById(id);
    }

    @Override
    public User update(User user) {
        return this.userRepository.saveAndFlush(user);
    }

    @Override
    public void deleteById(Integer id) {
        this.userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmail(s);
        if (user == null){
            LOG.warn(s + " could not be found");
            throw new UsernameNotFoundException(s + " not found");
        }
        return UserPrincipal.create(user);
    }
}
