package org.patinanetwork.codebloom.common.utils.function;

@FunctionalInterface
public interface ThrowableProcedure {
    void run() throws Exception;
}
