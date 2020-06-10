package com.blog.javablogging.config.securityconfig;

import com.blog.javablogging.config.jwtconfig.JwtAuthenticationEntryPoint;
import com.blog.javablogging.config.jwtconfig.JwtRequestFilter;
import com.blog.javablogging.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtRequestFilter requestFilter;
    private final UserService userDetailsService;
    private final PasswordEncrypt passwordEncrypt;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    public WebSecurityConfig(JwtRequestFilter requestFilter,
                             PasswordEncrypt passwordEncrypt,
                             JwtAuthenticationEntryPoint authenticationEntryPoint,
                             UserService userDetailsService) {
        this.requestFilter = requestFilter;
        this.userDetailsService = userDetailsService;
        this.passwordEncrypt = passwordEncrypt;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private BCryptPasswordEncoder passwordEncoder(){
        return this.passwordEncrypt.passwordEncoder();
    }

    protected void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .formLogin()
                .disable()
                .httpBasic()
                .disable()
                .exceptionHandling()
                    .authenticationEntryPoint(this.authenticationEntryPoint)
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                    .antMatchers("/api/user/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated();
        http.addFilterBefore(this.requestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
