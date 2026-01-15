package org.patinanetwork.codebloom.common.db.models.question.topic;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.patinanetwork.codebloom.common.db.helper.annotations.NotNullColumn;
import org.patinanetwork.codebloom.common.db.helper.annotations.NullColumn;

@Getter
@Setter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class QuestionTopic {

    @NotNullColumn
    private String id;

    @NullColumn
    private String questionId;

    @NullColumn
    private String questionBankId;

    @NotNullColumn
    private String topicSlug;

    @NotNullColumn
    private LeetcodeTopicEnum topic;

    @NotNullColumn
    private LocalDateTime createdAt;
}
