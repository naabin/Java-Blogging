package com.blog.javablogging.repository;

import com.blog.javablogging.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);
    Optional<User> findUserByName(String name);
    Optional<User> findUserByResetPin(Integer resetPin);
}
