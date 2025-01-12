package com.patina.codebloom.api.submissions;

import com.patina.codebloom.common.db.models.Submission;
import com.patina.codebloom.submissions.SubmissionsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionsController {
    private final SubmissionsService submissionsService;

    public SubmissionsController(SubmissionsService submissionsService) {
        this.submissionsService = submissionsService;
    }

    @GetMapping("/{leetcodeUsername}")
    public List<Submission> getSubmissionsByUsername(@PathVariable String leetcodeUsername) {
        return submissionsService.fetchAndSaveSubmissions(leetcodeUsername);
    }
}
