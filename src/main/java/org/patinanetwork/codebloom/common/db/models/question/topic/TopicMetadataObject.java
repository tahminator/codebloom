package org.patinanetwork.codebloom.common.db.models.question.topic;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TopicMetadataObject {
    private final String name;
    private final List<String> aliases;
}
