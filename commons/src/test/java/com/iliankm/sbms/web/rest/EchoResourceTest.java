package com.iliankm.sbms.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.google.common.net.HttpHeaders;
import com.iliankm.sbms.config.ApplicationPropertiesTestConfig;
import com.iliankm.sbms.config.WebSecurityConfig;
import com.iliankm.sbms.enums.Role;
import com.iliankm.sbms.jwt.JwtUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(value = EchoResource.class, secure = true)
@ContextConfiguration(classes= {EchoResource.class})
@Import({ApplicationPropertiesTestConfig.class, WebSecurityConfig.class})
@ActiveProfiles({"test"})
public class EchoResourceTest {

    private static final String NO_AUTH_URL = "/api-no-auth/v1/echo";
    
    private static final String URL = "/api/v1/echo";
    
    private static final String QUERY_PARAM_NAME = "q";
    
    @Autowired
    private MockMvc mvc;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void try_No_Auth_Without_Query_Param_Test() throws Exception {
        mvc.perform(get(NO_AUTH_URL).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    public void try_No_Auth_With_Query_Param_Test() throws Exception {
        mvc.perform(get(NO_AUTH_URL).param(QUERY_PARAM_NAME, "aaa").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andExpect(content().string("aaa"));
    }
    
    @Test
    public void try_Without_Auth_Test() throws Exception {
        mvc.perform(get(URL).param(QUERY_PARAM_NAME, "aaa").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    public void try_With_Auth_And_No_Roles_Test() throws Exception {
        String jwt =jwtUtil.createAccessToken("USER", new HashSet<>(Arrays.asList(Role.USER.getSpringSecurityName())));
        mvc.perform(get(URL).header(HttpHeaders.AUTHORIZATION, jwt).param(QUERY_PARAM_NAME, "aaa").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void try_With_Auth_And_OK_Test() throws Exception {
        String jwt =jwtUtil.createAccessToken("USER", new HashSet<>(Arrays.asList(Role.TEST.getSpringSecurityName())));
        mvc.perform(get(URL).header(HttpHeaders.AUTHORIZATION, jwt).param(QUERY_PARAM_NAME, "aaa").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("aaa"));
    }

}
