package com.nursematch.provider.controller;

import com.nursematch.provider.dto.AvailabilitySlotDTO;
import com.nursematch.provider.dto.ProviderProfileRequest;
import com.nursematch.provider.dto.ProviderProfileResponse;
import com.nursematch.provider.service.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/provider")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping("/profile")
    public ResponseEntity<ProviderProfileResponse> createProfile(@RequestBody @Valid ProviderProfileRequest req, Authentication auth) {
        return ResponseEntity.ok(providerService.createProfile(auth.getName(), req));
    }

    @GetMapping("/profile")
    public ResponseEntity<ProviderProfileResponse> getProfile(Authentication auth) {
        return ResponseEntity.ok(providerService.getProfile(auth.getName()));
    }

    @PostMapping("/availability")
    public ResponseEntity<ProviderProfileResponse> addAvailability(@RequestBody @Valid AvailabilitySlotDTO dto, Authentication auth) {
        return ResponseEntity.ok(providerService.addAvailability(auth.getName(), dto));
    }

    @PatchMapping("/accepting")
    public ResponseEntity<ProviderProfileResponse> toggleAccepting(@RequestParam boolean accepting, Authentication auth) {
        return ResponseEntity.ok(providerService.toggleAccepting(auth.getName(), accepting));
    }
}