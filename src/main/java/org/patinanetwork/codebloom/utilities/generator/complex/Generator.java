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
}
