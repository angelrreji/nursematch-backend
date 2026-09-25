package com.nursematch.match.dto;

import com.nursematch.match.model.MatchStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MatchResponse {
    private String id;
    private String studentId;
    private String providerId;
    private String specialty;
    private LocalDate startDate;
    private LocalDate endDate;
    private MatchStatus status;
    private boolean depositPaid;
}