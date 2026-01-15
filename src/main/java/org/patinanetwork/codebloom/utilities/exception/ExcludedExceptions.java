package org.patinanetwork.codebloom.utilities.exception;

import java.util.Set;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

public class ExcludedExceptions {

    private static final Set<Class<?>> EXCEPTIONS =
            Set.of(NoResourceFoundException.class, HttpRequestMethodNotSupportedException.class);

    public static boolean isValid(final Throwable throwable) {
        if (throwable == null) {
            return false;
        }

        for (Class<?> exceptionClasses : EXCEPTIONS) {
            if (exceptionClasses.isAssignableFrom(throwable.getClass())) {
                return false;
            }
        }

        return true;
    }
}
