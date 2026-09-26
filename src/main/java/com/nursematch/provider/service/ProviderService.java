package com.nursematch.provider.service;

import com.nursematch.exception.ResourceNotFoundException;
import com.nursematch.geo.GeocodingClient;
import com.nursematch.geo.dto.GeocodeResult;
import com.nursematch.provider.dto.AvailabilitySlotDTO;
import com.nursematch.provider.dto.ProviderProfileRequest;
import com.nursematch.provider.dto.ProviderProfileResponse;
import com.nursematch.provider.model.AvailabilitySlot;
import com.nursematch.provider.model.ProviderProfile;
import com.nursematch.provider.repository.ProviderProfileRepository;
import com.nursematch.user.model.User;
import com.nursematch.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderProfileRepository providerRepository;
    private final UserRepository userRepository;
    private final GeocodingClient geocodingClient;

    public ProviderProfileResponse createProfile(String email, ProviderProfileRequest req) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (providerRepository.existsByUserId(user.getId())) {
            throw new IllegalStateException("Provider profile already exists");
        }

        GeocodeResult geo = geocodingClient.geocode(
                req.getAddress(), req.getCity(), req.getState()
        );

        ProviderProfile profile = new ProviderProfile();
        profile.setUserId(user.getId());
        profile.setCredentials(req.getCredentials());
        profile.setLicenseState(req.getLicenseState());
        profile.setSpecialties(req.getSpecialties());
        profile.setCity(req.getCity());
        profile.setState(req.getState());
        profile.setLat(geo.getLat());     // ← real value now
        profile.setLng(geo.getLng());     // ← real value now

        providerRepository.save(profile);

        user.setProfileComplete(true);
        userRepository.save(user);

        return toResponse(profile);
    }

    public ProviderProfileResponse getProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ProviderProfile profile = providerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        return toResponse(profile);
    }

    public ProviderProfileResponse updateProfile(String email, ProviderProfileRequest req) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ProviderProfile profile = providerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        GeocodeResult geo = geocodingClient.geocode(
                req.getAddress(), req.getCity(), req.getState()
        );

        profile.setCredentials(req.getCredentials());
        profile.setLicenseState(req.getLicenseState());
        profile.setSpecialties(req.getSpecialties());
        profile.setCity(req.getCity());
        profile.setState(req.getState());
        profile.setLat(geo.getLat());
        profile.setLng(geo.getLng());

        providerRepository.save(profile);

        return toResponse(profile);
    }

    public ProviderProfileResponse addAvailability(String email, AvailabilitySlotDTO dto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ProviderProfile profile = providerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setStartDate(dto.getStartDate());
        slot.setEndDate(dto.getEndDate());
        slot.setSlotsOpen(dto.getSlotsOpen());

        profile.getAvailability().add(slot);
        providerRepository.save(profile);

        return toResponse(profile);
    }

    public ProviderProfileResponse toggleAccepting(String email, boolean accepting) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ProviderProfile profile = providerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        profile.setAcceptingStudents(accepting);
        providerRepository.save(profile);

        return toResponse(profile);
    }

    private ProviderProfileResponse toResponse(ProviderProfile p) {
        List<AvailabilitySlotDTO> slots = p.getAvailability().stream()
                .map(s -> {
                    AvailabilitySlotDTO dto = new AvailabilitySlotDTO();
                    dto.setStartDate(s.getStartDate());
                    dto.setEndDate(s.getEndDate());
                    dto.setSlotsOpen(s.getSlotsOpen());
                    return dto;
                })
                .collect(Collectors.toList());

        return new ProviderProfileResponse(
                p.getId(),
                p.getUserId(),
                p.getCredentials(),
                p.getLicenseState(),
                p.getSpecialties(),
                p.getCity(),
                p.getState(),
                p.isAcceptingStudents(),
                slots
        );
    }
}