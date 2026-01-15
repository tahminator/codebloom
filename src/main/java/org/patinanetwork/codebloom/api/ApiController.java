package org.patinanetwork.codebloom.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.patinanetwork.codebloom.common.dto.ApiResponder;
import org.patinanetwork.codebloom.utilities.ServerMetadataObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Basic server metadata")
public class ApiController {

    @Operation(summary = "Basic metadata about the server")
    @GetMapping
    public ResponseEntity<ApiResponder<ServerMetadataObject>> apiIndex(final HttpServletRequest request) {
        return ResponseEntity.ok().body(ApiResponder.success("Hello from Codebloom!", new ServerMetadataObject()));
    }
}
