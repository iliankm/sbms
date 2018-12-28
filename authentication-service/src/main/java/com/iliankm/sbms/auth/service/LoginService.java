package com.iliankm.sbms.auth.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.iliankm.sbms.auth.dto.JwtDTO;
import com.iliankm.sbms.auth.dto.LoginDTO;
import com.iliankm.sbms.exception.UnauthorizedException;
import com.iliankm.sbms.jwt.JwtUtil;

@Service
@PropertySource(value = "classpath:users.properties")
@ConfigurationProperties("")
public class LoginService {
    
    private static final String MSG_INVALID_USERNAME_PASSWORD = "Invalid username or password.";

    private final JwtUtil jwtUtil;

    private final Map<String, String> users = new HashMap<>();

    private final Map<String, String> roles = new HashMap<>();

    public LoginService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public JwtDTO login(LoginDTO loginDTO) {

        if (users.containsKey(loginDTO.getUsername())) {
            
            if (java.util.Base64.getEncoder().encodeToString(loginDTO.getPassword().getBytes())
                            .equals(users.get(loginDTO.getUsername()))) {
                
                String accessToken = jwtUtil.createAccessToken(loginDTO.getUsername(), getUserRoles(loginDTO.getUsername()));
                String refreshToken = jwtUtil.createRefreshToken(loginDTO.getUsername());

                return new JwtDTO(accessToken, refreshToken);
            }
        }
        
        throw new UnauthorizedException(MSG_INVALID_USERNAME_PASSWORD);
    }

    public Map<String, String> getUsers() {
        return users;
    }

    public Map<String, String> getRoles() {
        return roles;
    }

    private Set<String> getUserRoles(String username) {
        
        String rolesString = roles.get(username);
        
        return StringUtils.isEmpty(rolesString) ? Collections.emptySet()
                        : new HashSet<>(Arrays.asList(rolesString.split(",")));
    }
}
