package com.iliankm.sbms.auth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.iliankm.sbms.auth.dto.JwtDTO;
import com.iliankm.sbms.auth.dto.LoginDTO;
import com.iliankm.sbms.exception.UnauthorizedException;
import com.iliankm.sbms.jwt.JwtUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

@Service
@PropertySource(value = "classpath:users.properties")
@ConfigurationProperties
@Validated
public class LoginService {

    private static final String MSG_INVALID_USERNAME_PASSWORD = "Invalid username or password.";
    private static final String MSG_INVALID_REFRESH_TOKEN = "Invalid refresh token.";
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final JwtUtils jwtUtil;
    private final Map<String, String> users = new HashMap<>();
    private final Map<String, String> roles = new HashMap<>();

    @Autowired
    public LoginService(JwtUtils jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public JwtDTO login(@Valid @NotNull LoginDTO loginDTO) {
        String passwordHash = users.get(loginDTO.getUsername());
        if (passwordHash != null) {
            if (DigestUtils.sha256Hex(loginDTO.getPassword()).equals(passwordHash)) {
                String accessToken = jwtUtil.createAccessToken(loginDTO.getUsername(),
                        getUserRoles(loginDTO.getUsername()));
                String refreshToken = jwtUtil.createRefreshToken(loginDTO.getUsername());

                return new JwtDTO(accessToken, refreshToken);
            }
        }

        throw new UnauthorizedException(MSG_INVALID_USERNAME_PASSWORD);
    }

    public JwtDTO refresh(String refreshToken) {
        DecodedJWT decodedJwt;
        try {
            decodedJwt = jwtUtil.decodeToken(refreshToken);
        } catch (Exception ex) {
            log.warn(ex.getMessage());
            throw new UnauthorizedException(MSG_INVALID_REFRESH_TOKEN);
        }

        if (jwtUtil.isRefreshToken(decodedJwt)) {
            String username = decodedJwt.getSubject();
            String accessToken = jwtUtil.createAccessToken(username, getUserRoles(username));
            String newRefreshToken = jwtUtil.createRefreshToken(username);

            return new JwtDTO(accessToken, newRefreshToken);
        }

        throw new UnauthorizedException(MSG_INVALID_REFRESH_TOKEN);
    }

    public Map<String, String> getUsers() {
        return users;
    }

    public Map<String, String> getRoles() {
        return roles;
    }

    private Set<String> getUserRoles(String username) {
        String rolesString = roles.get(username);
        return StringUtils.isEmpty(rolesString) ? Collections.emptySet() : Set.of(rolesString.split(","));
    }
}
