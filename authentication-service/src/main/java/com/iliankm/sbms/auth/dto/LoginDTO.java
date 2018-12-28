package com.iliankm.sbms.auth.dto;

import java.io.Serializable;
import javax.validation.constraints.NotEmpty;

public class LoginDTO implements Serializable {
    
    private static final long serialVersionUID = -342721006264186532L;

    @NotEmpty
    private String username;
    
    @NotEmpty
    private String password;
    
    @SuppressWarnings("unused")
    private LoginDTO() {}
    
    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}