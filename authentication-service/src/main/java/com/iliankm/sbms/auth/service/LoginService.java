package com.iliankm.sbms.auth.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.hash.Hashing;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.iliankm.sbms.auth.dto.JwtDTO;
import com.iliankm.sbms.auth.dto.LoginDTO;
import com.iliankm.sbms.exception.UnauthorizedException;
import com.iliankm.sbms.jwt.JwtUtils;

@Service
@PropertySource(value = "classpath:users.properties")
@ConfigurationProperties("")
public class LoginService {

    private static final String MSG_INVALID_USERNAME_PASSWORD = "Invalid username or password.";
    private final JwtUtils jwtUtil;
    private final Map<String, String> users = new HashMap<>();
    private final Map<String, String> roles = new HashMap<>();

    public LoginService(JwtUtils jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public JwtDTO login(LoginDTO loginDTO) {
        if (loginDTO != null && StringUtils.hasText(loginDTO.getUsername())
                        && StringUtils.hasText(loginDTO.getPassword())) {
            String passwordHash = users.get(loginDTO.getUsername());
            if (passwordHash != null) {
                if (Hashing.sha256().hashString(loginDTO.getPassword(), StandardCharsets.UTF_8).toString().equals(passwordHash)) {
                    String accessToken = jwtUtil.createAccessToken(loginDTO.getUsername(),
                            getUserRoles(loginDTO.getUsername()));
                    String refreshToken = jwtUtil.createRefreshToken(loginDTO.getUsername());

                    return new JwtDTO(accessToken, refreshToken);
                }
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
