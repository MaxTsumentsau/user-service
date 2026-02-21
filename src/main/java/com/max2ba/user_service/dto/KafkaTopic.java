package com.max2ba.user_service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KafkaTopic {
     USER_EVENTS("user-events");
     private final String topicName;
}

