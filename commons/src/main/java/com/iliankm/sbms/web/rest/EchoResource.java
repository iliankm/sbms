package com.iliankm.sbms.web.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.iliankm.sbms.utils.RequestAttributesUtil;

@RestController
@RequestMapping("")
public class EchoResource {
    
    private RequestAttributesUtil requestAttributesUtil;
    
    public EchoResource(RequestAttributesUtil requestAttributesUtil) {
        this.requestAttributesUtil = requestAttributesUtil;  
    }
    
    @GetMapping("api-no-auth/v1/echo")
    public String echoNoAuth(@RequestParam("q") String q) {
        return q;
    }
    
    @GetMapping("api-no-auth/v1/echo/correlation-id")
    public String echoCorrelationId() {
        return requestAttributesUtil.get(RequestAttributesUtil.CORRELATION_ID);
    }
    
    @PreAuthorize("hasAnyRole('ADMIN','TEST')")
    @GetMapping("api/v1/echo")
    public String echo(@RequestParam("q") String q) {
        return q;
    }

}
