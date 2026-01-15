package org.patinanetwork.codebloom.common.db.models.question.bank;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.patinanetwork.codebloom.common.db.helper.annotations.JoinColumn;
import org.patinanetwork.codebloom.common.db.helper.annotations.NotNullColumn;
import org.patinanetwork.codebloom.common.db.helper.annotations.NullColumn;
import org.patinanetwork.codebloom.common.db.models.question.QuestionDifficulty;
import org.patinanetwork.codebloom.common.db.models.question.topic.QuestionTopic;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class QuestionBank {

    @NotNullColumn
    private String id;

    @NotNullColumn
    private String questionSlug;

    @NotNullColumn
    private QuestionDifficulty questionDifficulty;

    @NotNullColumn
    private String questionTitle;

    @NotNullColumn
    private int questionNumber;

    @NotNullColumn
    private String questionLink;

    @NullColumn
    private String description;

    @NotNullColumn
    private float acceptanceRate;

    @NotNullColumn
    private OffsetDateTime createdAt;

    /** Join field, update/create with {@link QuestionTopicRepository} */
    @JoinColumn
    private List<QuestionTopic> topics;
}
