package com.nursematch.match.controller;

import com.nursematch.match.dto.CreateMatchRequest;
import com.nursematch.match.dto.MatchDetailsResponse;
import com.nursematch.match.dto.MatchResponse;
import com.nursematch.match.service.MatchDiscoveryService;
import com.nursematch.match.service.MatchService;
import com.nursematch.provider.dto.ProviderResultDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchDiscoveryService discoveryService;
    private final MatchService matchService;

    @GetMapping("/discover")
    public ResponseEntity<List<ProviderResultDTO>> discover(
            @RequestParam String requestId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        return ResponseEntity.ok(
                discoveryService.discover(auth.getName(), requestId, page, size)
        );
    }

    @PostMapping("/create")
    public ResponseEntity<MatchResponse> createMatch(
            @RequestBody @Valid CreateMatchRequest req,
            Authentication auth) {
        return ResponseEntity.ok(matchService.createMatch(auth.getName(), req));
    }

    @GetMapping("/my-matches")
    public ResponseEntity<List<MatchResponse>> getMyMatches(Authentication auth) {
        return ResponseEntity.ok(matchService.getMyMatches(auth.getName()));
    }

    @GetMapping("/{matchId}/details")
    public ResponseEntity<MatchDetailsResponse> getDetails(
            @PathVariable String matchId,
            Authentication auth) {
        return ResponseEntity.ok(matchService.getDetails(auth.getName(), matchId));
    }
}