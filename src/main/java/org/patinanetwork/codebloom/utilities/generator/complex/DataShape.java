package org.patinanetwork.codebloom.utilities.generator.complex;

public enum DataShape {
    /** Expects {@code Map<T, List<T>>} where T is an enum class. */
    ENUM_TO_ENUM_LIST,

    /** Expects {@code Map<T, Map<String, String>>} where T is an enum class. */
    ENUM_TO_STRING_VALUE_MAP,
}
