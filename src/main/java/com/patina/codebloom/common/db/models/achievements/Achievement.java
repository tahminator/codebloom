package com.patina.codebloom.common.db.models.achievements;

import java.time.LocalDateTime;

import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;
import com.patina.codebloom.common.db.helper.annotations.NullColumn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Achievement {

    @NotNullColumn
    private String id;

    @NotNullColumn
    private String userId;

    @NullColumn
    private String iconUrl;

    @NotNullColumn
    private String title;

    @NullColumn
    private String description;

    @Builder.Default
    @NotNullColumn
    private boolean isActive = true;

    @NotNullColumn
    private LocalDateTime createdAt;

    @NullColumn
    private LocalDateTime deletedAt;
}
