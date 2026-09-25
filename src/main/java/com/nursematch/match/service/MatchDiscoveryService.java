package com.nursematch.match.service;

import com.nursematch.geo.GeoUtils;
import com.nursematch.provider.dto.ProviderResultDTO;
import com.nursematch.provider.model.AvailabilitySlot;
import com.nursematch.provider.model.ProviderProfile;
import com.nursematch.provider.repository.ProviderProfileRepository;
import com.nursematch.rotation.model.RotationRequest;
import com.nursematch.student.model.StudentProfile;
import com.nursematch.student.repository.StudentProfileRepository;
import com.nursematch.user.model.User;
import com.nursematch.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchDiscoveryService {

    private final ProviderProfileRepository providerRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final GeoUtils geoUtils;

    public List<ProviderResultDTO> discover(String email, String requestId,
                                            int page, int size) {

        // 1. Load student + rotation request
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(student.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        RotationRequest request = profile.getRotationRequests().stream()
                .filter(r -> r.getId().equals(requestId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Rotation request not found"));

        // 2. DB filter — hits compound index {specialties, state, acceptingStudents}
        List<ProviderProfile> candidates = providerRepository
                .findBySpecialtiesContainingAndAcceptingStudentsTrueAndState(
                        request.getSpecialty(), request.getPreferredState()
                );

        // 3. Availability overlap filter (in-memory)
        candidates = candidates.stream()
                .filter(p -> p.getAvailability().stream().anyMatch(slot ->
                        overlaps(slot, request)
                ))
                .collect(Collectors.toList());

        // 4. Distance scoring (Haversine)
        List<ProviderResultDTO> results = candidates.stream()
                .map(p -> {
                    double distance = geoUtils.haversineDistance(
                            request.getLat(), request.getLng(),
                            p.getLat(), p.getLng()
                    );
                    return new ProviderResultDTO(
                            p.getId(), p.getUserId(), p.getSpecialties(),
                            p.getCity(), p.getState(), distance
                    );
                })
                .sorted(Comparator.comparingDouble(ProviderResultDTO::getDistanceMiles))
                .collect(Collectors.toList());

        // 5. Manual pagination
        int start = Math.min(page * size, results.size());
        int end = Math.min(start + size, results.size());

        return results.subList(start, end);
    }

    private boolean overlaps(AvailabilitySlot slot, RotationRequest request) {
        return !slot.getStartDate().isAfter(request.getStartDate())
                && !slot.getEndDate().isBefore(request.getEndDate())
                && slot.getSlotsOpen() > 0;
    }
}