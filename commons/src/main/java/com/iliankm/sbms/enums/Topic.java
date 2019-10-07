package com.iliankm.sbms.enums;

/**
 * Enumeration for kafka topics
 */
public enum Topic {
    
    TEST(Names.TOPIC_TEST),
    TRANSACTIONAL_A(Names.TOPIC_TRANSACTIONAL_A),
    TRANSACTIONAL_B(Names.TOPIC_TRANSACTIONAL_B);
    
    private String topicName;
    
    Topic(String topicName) {
        this.topicName = topicName;
    }
    
    public String getTopicName() {
        return topicName;
    }
    
    public interface Names {
        String TOPIC_TEST = "ikm.test";
        String TOPIC_TRANSACTIONAL_A = "ikm.transactional.a";
        String TOPIC_TRANSACTIONAL_B = "ikm.transactional.b";
    }
}
