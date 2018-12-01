package com.iliankm.sbms.exception;

public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = -8403931918163376591L;
    
    public NotFoundException(String message) {
        super(message);
    }
}
