package com.patina.codebloom.common.db.models.achievements;

import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum AchievementPlaceEnum {
    ONE(1),
    TWO(2),
    THREE(3);

    private final int integerRepresentation;

    AchievementPlaceEnum(final int integerRepresentation) {
        this.integerRepresentation = integerRepresentation;
    }

    public static AchievementPlaceEnum fromInteger(
        final int integerRepresentation
    ) {
        return Stream.of(AchievementPlaceEnum.values())
            .filter(e -> e.getIntegerRepresentation() == integerRepresentation)
            .findFirst()
            .orElseThrow();
    }
}
