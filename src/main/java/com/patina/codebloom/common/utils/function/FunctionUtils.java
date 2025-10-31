package com.patina.codebloom.common.utils.function;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FunctionUtils {
    /**
     * Given a function `fn`, this method will try to get a value from the function.
     * If null, it will try again.
     *
     * @apiNote This will not catch or retry on exceptions.
     */
    public static <T> T tryAgainIfNull(final Supplier<T> fn) {
        return tryAgainIfFail(fn, (res) -> res != null, () -> {
        });
    }

    /**
     * Given a function `fn`, this method will try to get a value from the function.
     *
     * The second parameter `isSuccessfulFn` is a function that will determine the
     * condition that the result is successful or not. If this function returns
     * false, it will try again.
     *
     * @apiNote This will not catch or retry on exceptions.
     */
    public static <T> T tryAgainIfFail(final Supplier<T> fn, final Function<T, Boolean> isSuccessfulFn) {
        return tryAgainIfFail(fn, isSuccessfulFn, () -> {
        });
    }

    /**
     * Given a function `fn`, this method will try to get a value from the function.
     *
     * The second parameter `isSuccessfulFn` is a function that will determine the
     * condition that the result is successful or not. If this function returns
     * false, it will try again.
     *
     * If you would like to run a side-effect between the first and second attempt,
     * you may define `fnInBetween`. This function's input is the result of the
     * first attempt, so you may use it to help aide your side-effect.
     *
     * @apiNote This will not catch or retry on exceptions.
     */
    public static <T> T tryAgainIfFail(final Supplier<T> fn, final Function<T, Boolean> isSuccessfulFn, final Consumer<T> fnInBetween) {
        T res = fn.get();
        if (!isSuccessfulFn.apply(res)) {
            fnInBetween.accept(res);
            res = fn.get();
        }
        return res;
    }

    /**
     * Given a function `fn`, this method will try to get a value from the function.
     *
     * The second parameter `isSuccessfulFn` is a function that will determine the
     * condition that the result is successful or not. If this function returns
     * false, it will try again.
     *
     * If you would like to run a side-effect between the first and second attempt,
     * you may define `fnInBetween`.
     *
     * @apiNote This will not catch or retry on exceptions.
     */
    public static <T> T tryAgainIfFail(final Supplier<T> fn, final Function<T, Boolean> isSuccessfulFn, final Procedure fnInBetween) {
        T res = fn.get();
        if (!isSuccessfulFn.apply(res)) {
            fnInBetween.run();
            res = fn.get();
        }
        return res;
    }
}
