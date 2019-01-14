package com.iliankm.sbms.enums;

public enum Role {
    TEST, ADMIN, SYSTEM, USER;
    
    public String getSpringSecurityName() {
        return "ROLE_" + name();
    }
}
