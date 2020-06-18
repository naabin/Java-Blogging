package com.blog.javablogging.config.jwtconfig;

import com.blog.javablogging.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final UserService userService;
    private final JwtTokenUtil tokenUtil;

    @Autowired
    public JwtRequestFilter(UserService userService, JwtTokenUtil tokenUtil) {
        this.userService = userService;
        this.tokenUtil = tokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        String username = null;
        String token = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            token = authorizationHeader.substring(7);

            try {
                username = this.tokenUtil.getUsernameFromToken(token);
            } catch (IllegalArgumentException e){
                LOGGER.error("Unable to get Jwt token");
            } catch (ExpiredJwtException e){
                httpServletResponse.sendError(401, "Token has expired");
                LOGGER.warn("Token has expired");
            }
        } else {
            LOGGER.warn("JWT token does not begin with Bearer string");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = this.userService.loadUserByUsername(username);

            if (this.tokenUtil.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                /**
                 * After setting authentication in the context, we specify
                 * that the current user is authenticated. So it passes the Spring security
                 * configurations successfully.
                 */SecurityContext context = SecurityContextHolder.createEmptyContext();
                 context.setAuthentication(authenticationToken);
                 SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
