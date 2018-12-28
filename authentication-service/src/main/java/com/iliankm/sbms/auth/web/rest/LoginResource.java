package com.iliankm.sbms.auth.web.rest;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.iliankm.sbms.auth.dto.JwtDTO;
import com.iliankm.sbms.auth.dto.LoginDTO;
import com.iliankm.sbms.auth.service.LoginService;

@RestController
@RequestMapping("api/v1/login")
public class LoginResource {
    
    private final LoginService loginService;
    
    public LoginResource(LoginService loginService) {
        this.loginService = loginService;
    }
    
    @PostMapping("")
    public JwtDTO login(@Valid @RequestBody LoginDTO loginDTO) {
        return loginService.login(loginDTO);
    }

}
