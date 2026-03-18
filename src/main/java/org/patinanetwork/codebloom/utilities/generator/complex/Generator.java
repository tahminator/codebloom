package org.patinanetwork.codebloom.utilities.generator.complex;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Generator {
    private final String name;
    private final Object data;
    private final DataShape dataShape;
    /** Optional name for the generated TypeScript value type (used by some DataShapes). */
    private final String typeName;

    /** Optional TypeScript type name for the value type (used by ENUM_TO_OBJECT). */
    private final String objectClass;
}
