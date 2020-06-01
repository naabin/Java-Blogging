package com.blog.javablogging.service.impl;

import com.blog.javablogging.model.User;
import com.blog.javablogging.service.UserService;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    @Override
    public User loadUserByEmail(String email) {
        return null;
    }

    @Override
    public boolean uniqueUserAvailable(String username) {
        return false;
    }

    @Override
    public boolean uniqueEmailAvailable(String email) {
        return false;
    }

    @Override
    public Optional<User> findUserByResetPin(Integer pin) {
        return Optional.empty();
    }

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public Optional<User> getById(Integer id) {
        return Optional.empty();
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public void deleteById(Integer id) {

    }
}
