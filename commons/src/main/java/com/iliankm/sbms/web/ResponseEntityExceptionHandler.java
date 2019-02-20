package com.iliankm.sbms.web;

import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import com.iliankm.sbms.exception.NotFoundException;
import com.iliankm.sbms.exception.UnauthorizedException;

@ControllerAdvice
public class ResponseEntityExceptionHandler extends
                org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    private static final String MSG_FORMAT = "\r\n{} ({} {})";

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(Exception ex, HttpServletRequest request) {
        log.error(MessageFormatter.arrayFormat(MSG_FORMAT, new Object[] {ex.getMessage(),
                        request.getMethod(), request.getRequestURI()}).getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(Exception ex, HttpServletRequest request) {
        log.info(MSG_FORMAT, ex.getMessage(), request.getMethod(), request.getRequestURI());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<Object> handleUnauthorizedException(Exception ex, HttpServletRequest request) {
        log.info(MSG_FORMAT, ex.getMessage(), request.getMethod(), request.getRequestURI());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex, HttpServletRequest request) {
        log.info(MSG_FORMAT, ex.getMessage(), request.getMethod(), request.getRequestURI());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }
    
    /**
     * Needed for output bean validation messages to the response
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
                    MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status,
                    WebRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<String> errorMessages = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
    }
}
