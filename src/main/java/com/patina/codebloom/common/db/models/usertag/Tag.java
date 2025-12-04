package com.patina.codebloom.common.db.models.usertag;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** This is the base enum abstraction that's used in the UserTag model/table */
@AllArgsConstructor
@Getter
public enum Tag {
    Patina("Patina Network"),
    Hunter("Hunter College"),
    Nyu("New York University"),
    Baruch("Baruch College"),
    Rpi("Rensselaer Polytechnic Institute"),
    Gwc(null),
    Sbu("Stony Brook University"),
    Ccny("City College of New York"),
    Columbia("Columbia University"),
    Cornell("Cornell University"),
    Bmcc("Borough of Manhattan Community College");

    /** Only applies to school tags. */
    // CHECKSTYLE:OFF
    private final String resolvedName;
    // CHECKSTYLE:ON
}
