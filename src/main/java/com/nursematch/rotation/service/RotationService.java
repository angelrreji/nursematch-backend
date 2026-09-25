package com.nursematch.rotation.service;

import com.nursematch.exception.ResourceNotFoundException;
import com.nursematch.geo.GeocodingClient;
import com.nursematch.geo.dto.GeocodeResult;
import com.nursematch.rotation.dto.RotationRequestDTO;
import com.nursematch.rotation.model.RequestStatus;
import com.nursematch.rotation.model.RotationRequest;
import com.nursematch.student.model.StudentProfile;
import com.nursematch.student.repository.StudentProfileRepository;
import com.nursematch.user.model.User;
import com.nursematch.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RotationService {

    private final StudentProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final GeocodingClient geocodingClient;



    public List<RotationRequestDTO> getRequests(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return profile.getRotationRequests().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private RotationRequestDTO toDTO(RotationRequest r) {
        RotationRequestDTO dto = new RotationRequestDTO();
        dto.setId(r.getId());
        dto.setSpecialty(r.getSpecialty());
        dto.setPreferredCity(r.getPreferredCity());
        dto.setPreferredState(r.getPreferredState());
        dto.setStartDate(r.getStartDate());
        dto.setEndDate(r.getEndDate());
        dto.setHoursRequired(r.getHoursRequired());
        dto.setStatus(r.getStatus());
        return dto;
    }



    public RotationRequestDTO submitRequest(String email, RotationRequestDTO dto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StudentProfile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Complete your profile first"));

        // GEOCODE HERE
        GeocodeResult geo = geocodingClient.geocode(
                dto.getAddress(), dto.getPreferredCity(), dto.getPreferredState()
        );

        RotationRequest request = new RotationRequest();
        request.setId(UUID.randomUUID().toString());
        request.setSpecialty(dto.getSpecialty());
        request.setPreferredCity(dto.getPreferredCity());
        request.setPreferredState(dto.getPreferredState());
        request.setLat(geo.getLat());     // ← real value now
        request.setLng(geo.getLng());     // ← real value now
        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setHoursRequired(dto.getHoursRequired());
        request.setStatus(RequestStatus.PENDING);

        profile.getRotationRequests().add(request);
        profileRepository.save(profile);

        return toDTO(request);
    }
}