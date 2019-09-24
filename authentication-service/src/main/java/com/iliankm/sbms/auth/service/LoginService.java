package com.iliankm.sbms.auth.service;

import com.iliankm.sbms.auth.dto.JwtDTO;
import com.iliankm.sbms.auth.dto.LoginDTO;
import com.iliankm.sbms.exception.UnauthorizedException;
import com.iliankm.sbms.jwt.JwtUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

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
                if (DigestUtils.sha256Hex(loginDTO.getPassword()).equals(passwordHash)) {
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
