package com.nursematch.admin.service;

import com.nursematch.admin.dto.AdminMatchResponse;
import com.nursematch.exception.ResourceNotFoundException;
import com.nursematch.match.model.Match;
import com.nursematch.match.model.MatchStatus;
import com.nursematch.match.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MatchRepository matchRepository;

    public List<AdminMatchResponse> getAllMatches() {
        return matchRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AdminMatchResponse confirmMatch(String matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        if (!match.isDepositPaid()) {
            throw new RuntimeException("Cannot confirm match before deposit is paid");
        }

        match.setStatus(MatchStatus.CONFIRMED);
        match.setPaperworkComplete(true);
        matchRepository.save(match);

        return toResponse(match);
    }

    public AdminMatchResponse completeMatch(String matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        if (match.getStatus() != MatchStatus.CONFIRMED) {
            throw new RuntimeException("Match must be confirmed before completion");
        }

        match.setStatus(MatchStatus.COMPLETED);
        matchRepository.save(match);

        return toResponse(match);
    }

    public AdminMatchResponse cancelMatch(String matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        match.setStatus(MatchStatus.CANCELLED);
        matchRepository.save(match);

        return toResponse(match);
    }

    private AdminMatchResponse toResponse(Match m) {
        return new AdminMatchResponse(
                m.getId(), m.getStudentId(), m.getProviderId(), m.getSpecialty(),
                m.getStartDate(), m.getEndDate(), m.getStatus(),
                m.isDepositPaid(), m.isPaperworkComplete(), m.getCreatedAt()
        );
    }
}