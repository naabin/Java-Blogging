package com.blog.javablogging.config.securityconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class PasswordEncrypt {

    @Value("${bcrypt.salt}")
    private String salt;


    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12, new SecureRandom(salt.getBytes()));

    }

    public static String randomPassword(){
        String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder salt = new StringBuilder();

        Random random = new Random();
        while (salt.length() < 18){
            int index  = random.nextInt() * saltChars.length();
        }
        return salt.toString();
    }
}
