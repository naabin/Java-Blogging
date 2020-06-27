package com.blog.javablogging.controller;

import com.blog.javablogging.config.jwtconfig.JwtTokenUtil;
import com.blog.javablogging.dto.LoginRequest;
import com.blog.javablogging.dto.SignupRequest;
import com.blog.javablogging.model.User;
import com.blog.javablogging.model.security.oauth.AuthProvider;
import com.blog.javablogging.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil tokenUtil;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenUtil tokenUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenUtil = tokenUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) throws Exception {
        User user = this.userService.loadUserByEmail(request.getEmail());
        if (user == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials");
        }
        Authentication authentication = authenticate(request.getEmail(), request.getPassword());

        String token = this.tokenUtil.generateToken(authentication);
        return ResponseEntity.ok().header("token", token).body(user);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignupRequest request, Errors errors) throws Exception {
        if (errors.hasErrors()){
            List<ConstraintViolation<?>> violationList = new ArrayList<>();
            for (ObjectError e: errors.getAllErrors()){
                violationList.add(e.unwrap(ConstraintViolation.class));
            }
            StringBuilder errorMessage =  new StringBuilder();
            for (ConstraintViolation<?> violation: violationList){
                errorMessage.append(violation.getPropertyPath())
                        .append(" ").append(violation.getMessage()).append(", ");
            }
            throw new Exception("Request input is invalid: " + errorMessage);
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(request.getPassword());
        user.setProviderId(AuthProvider.local.toString());
        User newUser = this.userService.create(user);
        if(newUser == null){
            throw new Exception(request.getEmail() + " seems to be already a member.");
        }
        return ResponseEntity.ok().body(newUser);
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<?> getRefreshToken(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken) throws Exception {
        String s = this.tokenUtil.refreshToken(bearerToken.substring(7));
        return ResponseEntity.ok().header("token", s).build();
    }


    private Authentication authenticate(String email, String password) throws Exception {
        try{
            return this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e){
            throw  new Exception("User disabled", e);
        } catch (BadCredentialsException e){
            throw new BadCredentialsException("Invalid credentials", e);
        }
    }

}
