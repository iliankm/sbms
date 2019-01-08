package com.iliankm.sbms.auth.web.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.iliankm.sbms.auth.dto.JwtDTO;
import com.iliankm.sbms.auth.dto.LoginDTO;
import com.iliankm.sbms.auth.service.LoginService;
import com.iliankm.sbms.config.ApplicationPropertiesTestConfig;
import com.iliankm.sbms.config.WebSecurityConfig;
import com.iliankm.sbms.exception.UnauthorizedException;

@RunWith(SpringRunner.class)
@WebMvcTest(value = LoginResource.class, secure = true)
@Import({ApplicationPropertiesTestConfig.class, WebSecurityConfig.class})
@ActiveProfiles({"test"})
public class LoginResourceTest {

    private static final String BASE_URL = "/api-no-auth/v1/login";
    
    private static final String LOGIN_DTO = "{\"username\":\"%s\",\"password\":\"%s\"}";
    
    private static final String USERNAME = "USERNAME";
    
    private static final String PASSWORD = "PASSWORD";
    
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private LoginService loginService;
    
    @Test
    public void try_login_No_Request_Body_Test() throws Exception {
        mvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(""))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void try_login_No_Username_Test() throws Exception {
        mvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(String.format(LOGIN_DTO, "", PASSWORD)))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    public void try_login_No_Password_Test() throws Exception {
        mvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(String.format(LOGIN_DTO, USERNAME, "")))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    public void try_login_With_Incorrect_Credentials_Test() throws Exception {
        //given
        when(loginService.login(any())).thenThrow(UnauthorizedException.class);
        //when
        ResultActions resultActions =
                        mvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
                                        .content(String.format(LOGIN_DTO, USERNAME, PASSWORD)));
        //then
        resultActions.andExpect(status().isUnauthorized());
        //check loginService#login was called with correct arg
        ArgumentCaptor<LoginDTO> argumentCaptor = ArgumentCaptor.forClass(LoginDTO.class);
        verify(loginService, times(1)).login(argumentCaptor.capture());
        LoginDTO loginDTOArgument = argumentCaptor.getValue();
        assertEquals(USERNAME, loginDTOArgument.getUsername());
        assertEquals(PASSWORD, loginDTOArgument.getPassword());
    }    
    
    @Test
    public void login_OK_Test() throws Exception {
        //given
        when(loginService.login(any())).thenReturn(new JwtDTO(ACCESS_TOKEN, REFRESH_TOKEN));
        //when
        ResultActions resultActions =
                        mvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
                                        .content(String.format(LOGIN_DTO, USERNAME, PASSWORD)));
        //then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("accessToken").value(ACCESS_TOKEN))
            .andExpect(jsonPath("refreshToken").value(REFRESH_TOKEN));
        //check loginService#login was called with correct arg
        ArgumentCaptor<LoginDTO> argumentCaptor = ArgumentCaptor.forClass(LoginDTO.class);
        verify(loginService, times(1)).login(argumentCaptor.capture());
        LoginDTO loginDTOArgument = argumentCaptor.getValue();
        assertEquals(USERNAME, loginDTOArgument.getUsername());
        assertEquals(PASSWORD, loginDTOArgument.getPassword());
    }
}
