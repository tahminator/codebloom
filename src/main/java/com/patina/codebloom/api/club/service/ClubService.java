package com.patina.codebloom.api.club.service;

import org.springframework.security.crypto.password.PasswordEncoder;
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
        return new ClubDto(
                club.getId(),
                club.getName(),
                club.getDescription(),
                club.getSlug(),
                club.getSplashIconUrl(),
                club.getTag()
        );
    }

    public boolean isPasswordValid(Club club, String rawPassword) {
        return club.getPassword() == rawPassword;
    }
}
