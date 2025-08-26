package com.patina.codebloom.api.club.service;

import org.springframework.stereotype.Service;

import com.patina.codebloom.api.club.dto.ClubDto;
import com.patina.codebloom.common.db.models.club.Club;
import com.patina.codebloom.common.db.repos.club.ClubRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubService {
    private final ClubRepository clubRepository;

    public ClubDto getClubBySlug(String slug) {
        Club club = clubRepository.getClubBySlug(slug);
        if (club == null) return null;
        return ClubDto.builder()
            .id(club.getId())
            .name(club.getName())
            .description(club.getDescription())
            .slug(club.getSlug())
            .splashIconUrl(club.getSplashIconUrl())
            .tag(club.getTag())
            .build();
    }

    public boolean isPasswordValid(Club club, String rawPassword) {
        return club.getPassword() == rawPassword;
    }
}
