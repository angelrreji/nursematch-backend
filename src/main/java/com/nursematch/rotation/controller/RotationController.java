package com.nursematch.rotation.controller;

import com.nursematch.rotation.dto.RotationRequestDTO;
import com.nursematch.rotation.service.RotationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rotation")
@RequiredArgsConstructor
public class RotationController {

    private final RotationService rotationService;

    @PostMapping("/request")
    public ResponseEntity<RotationRequestDTO> submitRequest(
            @RequestBody @Valid RotationRequestDTO dto,
            Authentication auth) {
        return ResponseEntity.ok(rotationService.submitRequest(auth.getName(), dto));
    }

    @GetMapping("/requests")
    public ResponseEntity<List<RotationRequestDTO>> getRequests(Authentication auth) {
        return ResponseEntity.ok(rotationService.getRequests(auth.getName()));
    }
}