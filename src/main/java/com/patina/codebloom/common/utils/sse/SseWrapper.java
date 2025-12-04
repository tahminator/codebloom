package com.patina.codebloom.common.utils.sse;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.io.IOException;
import lombok.Getter;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Use {@code SseWrapper} instead of {@code SseEmitter} to:
 *
 * <ul>
 *   <li>Enforce type-safety in the backend.
 *   <li>“Trick” Springdoc into generating a concrete schema for SSE endpoints.
 * </ul>
 */
public class SseWrapper<T> extends SseEmitter {

    @Schema(requiredMode = RequiredMode.REQUIRED)
    @Getter
    @JsonUnwrapped
    private T data = null;

    public SseWrapper(final Long timeout) {
        super(timeout);
    }

    /** Please use {@code SseWrapper.sendData} */
    @Override
    @Deprecated
    public void send(final Object object) throws IOException {
        super.send(object, (MediaType) null);
    }

    public void sendData(final T object) throws IOException {
        super.send(object, (MediaType) null);
    }

    @Override
    @Hidden
    public Long getTimeout() {
        return null;
    }
}
