package com.patina.codebloom.common.db.models.potd;

import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class POTD {

    @NotNullColumn
    private String id;

    @NotNullColumn
    private String title;

    @NotNullColumn
    private String slug;

    @NotNullColumn
    private float multiplier;

    @NotNullColumn
    private LocalDateTime createdAt;
}
