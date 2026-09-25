package com.nursematch.admin.controller;

import com.nursematch.admin.dto.AdminMatchResponse;
import com.nursematch.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/matches")
    public ResponseEntity<List<AdminMatchResponse>> getAllMatches() {
        return ResponseEntity.ok(adminService.getAllMatches());
    }

    @PatchMapping("/matches/{id}/confirm")
    public ResponseEntity<AdminMatchResponse> confirm(@PathVariable String id) {
        return ResponseEntity.ok(adminService.confirmMatch(id));
    }

    @PatchMapping("/matches/{id}/complete")
    public ResponseEntity<AdminMatchResponse> complete(@PathVariable String id) {
        return ResponseEntity.ok(adminService.completeMatch(id));
    }

    @PatchMapping("/matches/{id}/cancel")
    public ResponseEntity<AdminMatchResponse> cancel(@PathVariable String id) {
        return ResponseEntity.ok(adminService.cancelMatch(id));
    }
}