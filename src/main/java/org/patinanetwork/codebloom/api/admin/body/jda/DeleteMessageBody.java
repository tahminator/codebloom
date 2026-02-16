package org.patinanetwork.codebloom.api.admin.body.jda;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class DeleteMessageBody {

    @NotNull
    private Long channelId;

    @NotNull
    private Long messageId;
}
