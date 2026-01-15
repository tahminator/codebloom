package org.patinanetwork.codebloom.utilities;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class AlternativeSwaggerController {

    /** This method is used by the frontend team. No need to document it. */
    @GetMapping("/swagger")
    @Operation(hidden = true)
    public RedirectView redirectToSwaggerHtml() {
        return new RedirectView("/swagger-ui/index.html");
    }
}
