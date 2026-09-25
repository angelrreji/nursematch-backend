package com.nursematch.student.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class StudentProfileResponse {
    private String id;
    private String userId;
    private String university;
    private String program;
    private LocalDate graduationDate;
}