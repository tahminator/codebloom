package com.patina.codebloom.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.leetcode.LeetcodeApiHandler;
import com.patina.codebloom.common.metadata.ServerMetadataObject;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@Tag(name = "Basic server metadata")
public class ApiController {

    private final LeetcodeApiHandler leetcodeApiHandler;

    public ApiController(LeetcodeApiHandler leetcodeApiHandler) {
        this.leetcodeApiHandler = leetcodeApiHandler;
    }

    @Operation(summary = "Basic metadata about the server")
    @GetMapping()
    public ResponseEntity<ApiResponder<ServerMetadataObject>> apiIndex(HttpServletRequest request) {
        leetcodeApiHandler.findQuestionBySlug("trapping-rain-water");
        return ResponseEntity.ok().body(ApiResponder.success("Hello from Codebloom!", new ServerMetadataObject()));
    }
}
