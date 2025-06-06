package com.patina.codebloom.common.schools;

import java.util.List;

public class SupportedSchools {
    public static List<SchoolEnum> getList() {
        return List.of(SchoolEnum.HUNTER, SchoolEnum.NYU);
    }
}
