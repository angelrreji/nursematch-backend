package com.nursematch.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentProfileRequest {

    @NotBlank
    @Size(max = 200)
    private String university;

    @NotBlank
    @Size(max = 200)
    private String program;

    @NotNull
    private LocalDate graduationDate;
}