package org.patinanetwork.codebloom.api.admin.body;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class DeleteAnnouncementBody {

    private String id;
}
