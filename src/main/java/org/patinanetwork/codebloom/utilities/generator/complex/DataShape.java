package org.patinanetwork.codebloom.utilities.generator.complex;

public enum DataShape {
    /** Expects {@code Map<T, List<T>>} where T is an enum class. */
    ENUM_TO_ENUM_LIST,

    /** Expects {@code Map<T, Object>} where T is an enum class. Uses Jackson to serialize values to JSON. */
    ENUM_TO_OBJECT,
}
