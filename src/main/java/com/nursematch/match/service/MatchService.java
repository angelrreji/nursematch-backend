package com.nursematch.match.service;

import com.nursematch.match.dto.CreateMatchRequest;
import com.nursematch.match.dto.MatchResponse;
import com.nursematch.match.model.Match;
import com.nursematch.match.repository.MatchRepository;
import com.nursematch.user.model.User;
import com.nursematch.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.nursematch.exception.PaymentRequiredException;
import com.nursematch.exception.ResourceNotFoundException;
import com.nursematch.match.dto.MatchDetailsResponse;
import com.nursematch.provider.model.ProviderProfile;
import com.nursematch.provider.repository.ProviderProfileRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final ProviderProfileRepository providerRepository;



    public MatchResponse createMatch(String email, CreateMatchRequest req) {

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Match match = new Match();
        match.setStudentId(student.getId());
        match.setProviderId(req.getProviderId());
        match.setRotationRequestId(req.getRotationRequestId());
        match.setSpecialty(req.getSpecialty());
        match.setStartDate(req.getStartDate());
        match.setEndDate(req.getEndDate());

        matchRepository.save(match);

        return toResponse(match);
    }

    public List<MatchResponse> getMyMatches(String email) {

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return matchRepository.findByStudentId(student.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private MatchResponse toResponse(Match m) {
        return new MatchResponse(
                m.getId(), m.getStudentId(), m.getProviderId(),
                m.getSpecialty(), m.getStartDate(), m.getEndDate(),
                m.getStatus(), m.isDepositPaid()
        );
    }

    public MatchDetailsResponse getDetails(String email, String matchId) {

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Match match = matchRepository.findByIdAndStudentId(matchId, student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        if (!match.isDepositPaid()) {
            throw new PaymentRequiredException("Deposit required to view match details");
        }

        ProviderProfile provider = providerRepository.findById(match.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        return new MatchDetailsResponse(
                match.getId(), match.getSpecialty(), match.getStartDate(), match.getEndDate(),
                match.getStatus(), match.isDepositPaid(), match.isPaperworkComplete(),
                provider.getCredentials(), provider.getCity(), provider.getState()
        );
    }
}