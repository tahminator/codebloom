package com.patina.codebloom.api.club;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.patina.codebloom.api.club.dto.ClubDto;
import com.patina.codebloom.api.club.service.ClubService;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.lag.FakeLag;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/club")
@Tag(name = "General Club Routes")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @GetMapping("/{clubSlug}")
    public ResponseEntity<ApiResponder<ClubDto>> getClubDataBySlug(@PathVariable final String clubSlug) {
        FakeLag.sleep(650);

        ClubDto club = clubService.getClubBySlug(clubSlug);

        if (club == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Club does not exist.");
        }
        
        return ResponseEntity.ok().body(ApiResponder.success("Club Found", club));
    }
    
}
