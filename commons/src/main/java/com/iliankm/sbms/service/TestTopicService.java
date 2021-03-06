package com.iliankm.sbms.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TestTopicService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    public void process(Map<String, String> message) {
        log.info("Message passed to BE service: " + message);
    }
}
