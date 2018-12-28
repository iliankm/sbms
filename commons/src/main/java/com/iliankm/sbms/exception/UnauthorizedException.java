package com.iliankm.sbms.exception;

public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = -8403931918163376591L;
    
    public UnauthorizedException(String message) {
        super(message);
    }
}
