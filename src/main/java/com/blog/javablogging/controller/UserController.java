package com.blog.javablogging.controller;

import com.blog.javablogging.exceptions.ResourceNotFoundException;
import com.blog.javablogging.model.User;
import com.blog.javablogging.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Integer id) throws ResourceNotFoundException {
        User user = this.userService.getById(id).orElseThrow(() -> new ResourceNotFoundException(id + " does not seem to be associated with any resource"));
        return ResponseEntity.ok().body(user);
    }
}
