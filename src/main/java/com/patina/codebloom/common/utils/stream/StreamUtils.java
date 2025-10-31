package com.patina.codebloom.common.utils.stream;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility class providing helper functions for stream operations.
 */
public class StreamUtils {
    /**
     * Creates a function that applies a side effect to an input value and returns
     * the value unchanged.
     *
     * This differs from {@link java.util.stream.Stream#peek} because the default
     * `peek` method is intended for debugging, and in some cases, may be abstracted
     * away from the compiler.
     *
     * NOTE: The order `peek` returns is not guaranteed on
     * {@link java.util.stream.Stream#parallel()}.
     *
     ** <pre>
     * {@code
     * users.stream()
     *                 .map(StreamUtils.peek(user -> System.out.println(user))) // will log and return each user back into the stream.
     *                 .filter(User::isAdmin) // can keep doing operations on the stream of users.
     *                 .toList()
     * }
     * </pre>
     *
     * @param <T> the type of the input and output value
     * @param fn the function to apply as a side effect on each input value.
     * @return a function that applies the side effect and returns the input value
     */
    public static <T> Function<T, T> peek(final Consumer<T> fn) {
        return t -> {
            fn.accept(t);
            return t;
        };
    }
}
