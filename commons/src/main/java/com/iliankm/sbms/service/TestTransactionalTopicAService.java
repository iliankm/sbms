package com.iliankm.sbms.service;

import com.iliankm.sbms.enums.Topic;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TestTransactionalTopicAService {

    private static final String SPECIAL_KEY = "SPECIAL_KEY";

    private final SenderService senderService;

    @Autowired
    public TestTransactionalTopicAService(SenderService senderService) {
        this.senderService = senderService;
    }

    public void process(Map<String, String> message) {
        senderService.sendTransactional(Pair.of(Topic.TRANSACTIONAL_B, message));

        if (StringUtils.isEmpty(message.get(SPECIAL_KEY))) {
            throw new RuntimeException("SPECIAL_KEY not passed.");
        }
    }
}
