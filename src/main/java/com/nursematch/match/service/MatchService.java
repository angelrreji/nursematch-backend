package com.nursematch.match.service;

import com.nursematch.match.dto.CreateMatchRequest;
import com.nursematch.match.dto.MatchResponse;
import com.nursematch.match.model.Match;
import com.nursematch.match.repository.MatchRepository;
import com.nursematch.rotation.model.RequestStatus;
import com.nursematch.rotation.model.RotationRequest;
import com.nursematch.student.model.StudentProfile;
import com.nursematch.student.repository.StudentProfileRepository;
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
    private final StudentProfileRepository studentProfileRepository;

    public MatchResponse createMatch(String email, CreateMatchRequest req) {

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        RotationRequest rotation = profile.getRotationRequests().stream()
                .filter(r -> r.getId().equals(req.getRotationRequestId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Rotation request not found"));

        if (rotation.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Rotation request is already matched");
        }

        ProviderProfile provider = providerRepository.findById(req.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        if (!provider.isAcceptingStudents()) {
            throw new IllegalStateException("Provider is not accepting students");
        }

        boolean hasCapacity = provider.getAvailability().stream()
                .anyMatch(slot -> !slot.getStartDate().isAfter(req.getStartDate())
                        && !slot.getEndDate().isBefore(req.getEndDate())
                        && slot.getSlotsOpen() > 0);
        if (!hasCapacity) {
            throw new IllegalStateException("Provider has no matching availability");
        }

        Match match = new Match();
        match.setStudentId(student.getId());
        match.setProviderId(provider.getId());
        match.setRotationRequestId(rotation.getId());
        match.setSpecialty(req.getSpecialty());
        match.setStartDate(req.getStartDate());
        match.setEndDate(req.getEndDate());

        matchRepository.save(match);

        rotation.setStatus(RequestStatus.MATCHED);
        studentProfileRepository.save(profile);

        return toResponse(match);
    }

    public List<MatchResponse> getMyMatches(String email) {

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

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