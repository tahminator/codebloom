package com.patina.codebloom.common.components;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * Exception class that is used to indicate a problem with a duel. This class was intended to be used as a compile-time
 * exception.
 *
 * <p>The class may have an {@code HttpStatus} which can be accessed at {@code getHttpStatus}, which can be used to
 * throw a responsive exception in a web environment (if not web, it can be safely ignored).
 */
@Getter
@ToString(callSuper = true)
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class DuelException extends Exception {

    private final Optional<HttpStatus> httpStatus;

    public DuelException(Throwable t) {
        httpStatus = Optional.empty();
        super("Duel exception occured.", t);
    }

    public DuelException(HttpStatus status, String message) {
        this.httpStatus = Optional.ofNullable(status);
        super(message);
    }

    @Override
    public void printStackTrace() {
        httpStatus.ifPresentOrElse(
                s -> {
                    log.error("Duel exception occured with HttpStatus of {}.", s);
                },
                () -> {
                    log.error("Duel exception occured.");
                });
        super.printStackTrace();
    }
}
