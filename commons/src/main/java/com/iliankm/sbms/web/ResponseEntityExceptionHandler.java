package com.iliankm.sbms.web;

import java.nio.file.AccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.iliankm.sbms.exception.NotFoundException;


@ControllerAdvice
public class ResponseEntityExceptionHandler extends
                org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({AlgorithmMismatchException.class, SignatureVerificationException.class,
                    InvalidClaimException.class, JWTDecodeException.class,
                    JWTVerificationException.class})
    public ResponseEntity<Object> handleJwtDecodeExceptions(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>("Invalid token.", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({TokenExpiredException.class})
    public ResponseEntity<Object> handleJwtTokenExpired(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>("Token expired.", HttpStatus.UNAUTHORIZED);
    }

}
