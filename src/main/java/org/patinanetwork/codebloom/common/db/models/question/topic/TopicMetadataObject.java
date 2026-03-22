package org.patinanetwork.codebloom.common.db.models.question.topic;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.patinanetwork.codebloom.shared.TypableObject;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TopicMetadataObject implements TypableObject {
    public static final String TS_TYPE = """
            export type TopicMetadataObject = {
              name: string;
              aliases?: string[];
            };

            """.stripIndent();

    private final String name;
    private final List<String> aliases;

    @Override
    public String tsType() {
        return TS_TYPE;
    }
}
