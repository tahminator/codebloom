package org.patinanetwork.codebloom.api.club.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.patinanetwork.codebloom.common.db.models.usertag.Tag;

@Value
@Getter
@Builder
public class ClubDto {

    private String id;
    private String name;
    private String description;
    private String slug;
    private String splashIconUrl;
    private Tag tag;
}
