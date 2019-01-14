package com.iliankm.sbms.web.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class EchoResource {
    
    @GetMapping("api-no-auth/v1/echo")
    public String echoNoAuth(@RequestParam("q") String q) {
        return q;
    }
    
    @PreAuthorize("hasAnyRole('ADMIN','TEST')")
    @GetMapping("api/v1/echo")
    public String echo(@RequestParam("q") String q) {
        return q;
    }

}
