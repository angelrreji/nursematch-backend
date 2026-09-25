package com.nursematch.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("Public endpoint — no auth needed");
    }

    @GetMapping("/secure")
    public ResponseEntity<String> secureEndpoint(Principal principal) {
        return ResponseEntity.ok("Secured! Logged in as: " + principal.getName());
    }
}