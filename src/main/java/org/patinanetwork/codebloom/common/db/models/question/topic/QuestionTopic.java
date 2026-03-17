package org.patinanetwork.codebloom.common.db.models.question.topic;

import java.time.LocalDateTime;
import java.util.Optional;
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
    private Optional<String> questionId;

    @NullColumn
    private Optional<String> questionBankId;

    @NotNullColumn
    private String topicSlug;

    @NotNullColumn
    private LeetcodeTopicEnum topic;

    @NotNullColumn
    private LocalDateTime createdAt;

    public static class QuestionTopicBuilder {
        public QuestionTopicBuilder questionId(String questionId) {
            this.questionId = Optional.ofNullable(questionId);
            return this;
        }

        public QuestionTopicBuilder questionBankId(String questionBankId) {
            this.questionBankId = Optional.ofNullable(questionBankId);
            return this;
        }

        public QuestionTopic build() {
            if (this.questionId == null) {
                this.questionId = Optional.empty();
            }
            if (this.questionBankId == null) {
                this.questionBankId = Optional.empty();
            }
            return new QuestionTopic(id, questionId, questionBankId, topicSlug, topic, createdAt);
        }
    }
}
