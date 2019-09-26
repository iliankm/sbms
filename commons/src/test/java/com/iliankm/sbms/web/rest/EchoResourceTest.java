package com.iliankm.sbms.web.rest;

import com.iliankm.sbms.config.ApplicationTestConfig;
import com.iliankm.sbms.config.FilterConfig;
import com.iliankm.sbms.config.WebSecurityConfig;
import com.iliankm.sbms.enums.Role;
import com.iliankm.sbms.jwt.JwtUtils;
import com.iliankm.sbms.utils.AppProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.HashSet;

import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = EchoResource.class)
@ContextConfiguration(classes= {EchoResource.class})
@Import({ApplicationTestConfig.class, WebSecurityConfig.class, FilterConfig.class})
@ActiveProfiles({"test"})
public class EchoResourceTest {

    private static final String NO_AUTH_URL = "/api-no-auth/v1/echo";
    private static final String CORRELATION_ID_ECHO_URL = "/api-no-auth/v1/echo/correlation-id";
    private static final String URL = "/api/v1/echo";
    private static final String QUERY_PARAM_NAME = "q";
    private static final String HEADER_CORRELATION_ID = "Correlation-Id";
    private static final String CORRELATION_ID = "d895bf77-0dee-470f-bb7e-4a7efcc749e3";
    
    @Autowired
    private MockMvc mvc;
    
    @Autowired
    private JwtUtils jwtUtil;
    
    @Test
    public void try_No_Auth_Without_Query_Param() throws Exception {
        mvc.perform(get(NO_AUTH_URL).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    public void try_No_Auth_With_Query_Param() throws Exception {
        mvc.perform(get(NO_AUTH_URL).param(QUERY_PARAM_NAME, "aaa").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andExpect(content().string("aaa"));
    }
    
    @Test
    public void try_Without_Auth() throws Exception {
        mvc.perform(get(URL).param(QUERY_PARAM_NAME, "aa").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    public void try_With_Invalid_JWT() throws Exception {
        mvc.perform(get(URL).header(HttpHeaders.AUTHORIZATION, "invalid-jwt")
                        .param(QUERY_PARAM_NAME, "aaa").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnauthorized());
    }
    
    @Test
    public void try_With_Expired_JWT() throws Exception {
        //given
        AppProperties applicationProperties = Mockito.mock(AppProperties.class);
        when(applicationProperties.jwtAccessTokenExpirationTime()).thenReturn(-1);
        when(applicationProperties.jwtSecret()).thenReturn("TEST_JWT_SECRET");
        JwtUtils jwtUtil = new JwtUtils(applicationProperties);
        String jwt = jwtUtil.createAccessToken("USER", new HashSet<>(Collections.singletonList(Role.TEST.name())));
        //when
        ResultActions result = mvc.perform(get(URL).header(HttpHeaders.AUTHORIZATION, jwt)
                        .param(QUERY_PARAM_NAME, "aaa").contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isUnauthorized());
    }    
    
    @Test
    public void try_With_Auth_And_No_Roles() throws Exception {
        //given
        String jwt = jwtUtil.createAccessToken("USER", new HashSet<>(Collections.singletonList(Role.USER.name())));
        //when
        ResultActions result = mvc.perform(get(URL).header(HttpHeaders.AUTHORIZATION, jwt)
                        .param(QUERY_PARAM_NAME, "aaa").contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    public void try_With_Auth_And_OK() throws Exception {
        //given
        String jwt = jwtUtil.createAccessToken("USER", new HashSet<>(Collections.singletonList(Role.TEST.name())));
        //when
        ResultActions result = mvc.perform(get(URL).header(HttpHeaders.AUTHORIZATION, jwt)
                        .param(QUERY_PARAM_NAME, "aaa").contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isOk());
        result.andExpect(content().string("aaa"));
    }
    
    @Test
    public void echo_Correlation_Id_Passed_In_Header() throws Exception {
        mvc.perform(get(CORRELATION_ID_ECHO_URL)
                        .header(HEADER_CORRELATION_ID, CORRELATION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().string(CORRELATION_ID));
    }
    
    @Test
    public void echo_Correlation_Id_Not_Passed_In_Header() throws Exception {
        mvc.perform(get(CORRELATION_ID_ECHO_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().string(not(isEmptyString())));
    }
}
