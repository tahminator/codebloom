package com.patina.codebloom.common.utils.function;

@FunctionalInterface
public interface ThrowableProcedure {
    void run() throws Exception;
}
