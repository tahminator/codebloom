package org.patinanetwork.codebloom.common.db.models.usertag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** This is the base enum abstraction that's used in the UserTag model/table */
@AllArgsConstructor
@Getter
@Schema(description = "Tag")
public enum Tag {
    Patina("Patina Network", "Patina", "Patina Network", "patina", "Patina Logo"),
    Hunter("Hunter College", "Hunter", "Hunter College", "hunter", "Hunter College Logo"),
    Nyu("New York University", "NYU", "New York University", "nyu", "NYU Logo"),
    Baruch("Baruch College", "Baruch", "Baruch College", "baruch", "Baruch College Logo"),
    Rpi("Rensselaer Polytechnic Institute", "RPI", "Rensselaer Polytechnic Institute", "rpi", "RPI Logo"),
    Gwc("GWC - Hunter College", "GWC - Hunter College", "GWC - Hunter College", "gwc", "GWC Logo"),
    Sbu("Stony Brook University", "SBU", "Stony Brook University", "sbu", "Stony Brook University Logo"),
    Ccny("City College of New York", "CCNY", "City College of New York", "ccny", "City College of New York Logo"),
    Columbia("Columbia University", "Columbia", "Columbia University", "columbia", "Columbia University Logo"),
    Cornell("Cornell University", "Cornell", "Cornell University", "cornell", "Cornell University Logo"),
    Bmcc(
            "Borough of Manhattan Community College",
            "BMCC",
            "Borough of Manhattan Community College",
            "bmcc",
            "BMCC Logo"),
    MHCPlusPlus("MHC++", "MHC++", "The MHC++ Club | Macaulay Hunter College", "mhcplusplus", "MHC++ Logo");

    // CHECKSTYLE:OFF
    /** Only applies to school tags. */
    private final String resolvedName;

    private final String shortName;
    private final String name;
    private final String apiKey;
    private final String alt;
    // CHECKSTYLE:ON
}
