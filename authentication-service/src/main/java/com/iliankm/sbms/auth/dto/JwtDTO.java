package com.iliankm.sbms.auth.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;

public class JwtDTO implements Serializable {
    
    private static final long serialVersionUID = 3030037646814701765L;
    
    @NotNull
    private String accessToken;
    
    @NotNull
    private String refreshToken;

    @SuppressWarnings("unused")
    private JwtDTO() {}
    
    public JwtDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
