package com.patina.codebloom.common.email;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;

/**
 * This class exists due to the fact that when the email connection is closed,
 * all Messages are subsequently closed as well.
 */
@Getter
@Builder
public class Message {
    private final String subject;
    private final String message;
    private final Date sentAt;
}
