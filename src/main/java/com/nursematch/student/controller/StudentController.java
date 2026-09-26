package com.nursematch.student.controller;

import com.nursematch.student.dto.StudentProfileRequest;
import com.nursematch.student.dto.StudentProfileResponse;
import com.nursematch.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/profile")
    public ResponseEntity<StudentProfileResponse> createProfile(@RequestBody @Valid StudentProfileRequest req, Authentication auth) {
        return ResponseEntity.ok(studentService.createProfile(auth.getName(), req));
    }

    @GetMapping("/profile")
    public ResponseEntity<StudentProfileResponse> getProfile(Authentication auth) {
        return ResponseEntity.ok(studentService.getProfile(auth.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<StudentProfileResponse> updateProfile(@RequestBody @Valid StudentProfileRequest req, Authentication auth) {
        return ResponseEntity.ok(studentService.updateProfile(auth.getName(), req));
    }
}