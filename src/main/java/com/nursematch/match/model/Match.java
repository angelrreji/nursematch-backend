package com.nursematch.match.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "matches")
@CompoundIndexes({
        @CompoundIndex(def = "{'studentId': 1, 'status': 1}"),
        @CompoundIndex(def = "{'providerId': 1, 'status': 1}")
})
@Data
public class Match {

    @Id
    private String id;

    private String studentId;
    private String providerId;
    private String rotationRequestId;
    private String specialty;

    private LocalDate startDate;
    private LocalDate endDate;

    private MatchStatus status = MatchStatus.PENDING;
    private boolean depositPaid = false;
    private boolean paperworkComplete = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}