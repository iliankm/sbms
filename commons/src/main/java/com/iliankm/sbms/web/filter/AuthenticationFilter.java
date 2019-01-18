package com.iliankm.sbms.web.filter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.iliankm.sbms.jwt.JwtUtil;
import com.iliankm.sbms.utils.RequestAttributesUtil;

public class AuthenticationFilter extends OncePerRequestFilter {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());
   
    private static final String HEADER_SWAGGER_AUTHORIZATION = "api_key";
    
    private static final String MSG_INVALID_TOKEN = "Invalid token.";
    
    private static final String MSG_TOKEN_EXPIRED = "Token expired.";

    private final JwtUtil jwtUtil;

    public AuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            //determine the auth. header
            String authorizationHeader =
                            ObjectUtils.firstNonNull(request.getHeader(HttpHeaders.AUTHORIZATION), request.getHeader(HEADER_SWAGGER_AUTHORIZATION));

            if (StringUtils.hasText(authorizationHeader)) {
                //try decode the token
                DecodedJWT decodedJwt;
                try {
                    decodedJwt = jwtUtil.decodeToken(authorizationHeader);    
                } catch (TokenExpiredException te) {
                    RequestAttributesUtil.set(RequestAttributesUtil.NO_AUTH_MESSAGE, MSG_TOKEN_EXPIRED);
                    log.warn(te.getMessage());
                    return;
                } catch (JWTVerificationException e) {
                    RequestAttributesUtil.set(RequestAttributesUtil.NO_AUTH_MESSAGE, MSG_INVALID_TOKEN);
                    log.error(e.getMessage());
                    return;
                }
                
                //set the jwt to thread-bound request attribute
                RequestAttributesUtil.set(RequestAttributesUtil.JWT, authorizationHeader);
                
                //roles as set of GrantedAuthority
                Set<GrantedAuthority> authorities = jwtUtil.getRoles(decodedJwt).stream()
                                .map(s -> "ROLE_" + s)
                                .map(s -> new SimpleGrantedAuthority(s))
                                .collect(Collectors.toSet());
                //create Spring Security Authentication
                UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(decodedJwt.getSubject(), null, authorities);
                
                //set Spring Security Authentication to the security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            
        } finally {
            filterChain.doFilter(request, response);                
        }
    }

}
