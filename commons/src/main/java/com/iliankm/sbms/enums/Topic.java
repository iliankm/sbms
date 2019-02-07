package com.iliankm.sbms.enums;

/**
 * Enumeration for kafka topics
 */
public enum Topic {
    
    TEST(Names.TOPIC_TEST);
    
    private String topicName;
    
    Topic(String topicName) {
        this.topicName = topicName;
    }
    
    public String getTopicName() {
        return topicName;
    }
    
    public static interface Names {
        String TOPIC_TEST = "ikm.test";
    }
    
}
