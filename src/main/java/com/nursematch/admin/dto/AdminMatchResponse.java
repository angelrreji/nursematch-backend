package com.nursematch.admin.dto;

import com.nursematch.match.model.MatchStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AdminMatchResponse {
    private String id;
    private String studentId;
    private String providerId;
    private String specialty;
    private LocalDate startDate;
    private LocalDate endDate;
    private MatchStatus status;
    private boolean depositPaid;
    private boolean paperworkComplete;
    private LocalDateTime createdAt;
}