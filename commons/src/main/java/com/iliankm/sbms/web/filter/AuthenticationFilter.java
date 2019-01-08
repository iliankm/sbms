package com.iliankm.sbms.web.filter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.iliankm.sbms.jwt.JwtUtil;

public class AuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    
    private static final String HEADER_SWAGGER_AUTHORIZATION = "api_key";

    private final JwtUtil jwtUtil;

    public AuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                    FilterChain filterChain) throws ServletException, IOException {

        //determine the auth. header
        String authorizationHeader =
                        ObjectUtils.firstNonNull(request.getHeader(HEADER_AUTHORIZATION), request.getHeader(HEADER_SWAGGER_AUTHORIZATION));

        if (StringUtils.hasText(authorizationHeader)) {
            //try decode the token
            DecodedJWT decodedJwt = jwtUtil.decodeToken(authorizationHeader);
            
            //set the jwt to thread-bound request attribute
            RequestContextHolder.getRequestAttributes().setAttribute("jwt", authorizationHeader, RequestAttributes.SCOPE_REQUEST);
            
            //roles as set of GrantedAuthority
            Set<GrantedAuthority> authorities = jwtUtil.getRoles(decodedJwt).stream().map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toSet());
            //create Spring Security Authentication
            UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(decodedJwt.getSubject(), null, authorities);
            
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            //set Spring Security Authentication to the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

}
