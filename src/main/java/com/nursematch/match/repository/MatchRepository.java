package com.nursematch.match.repository;

import com.nursematch.match.model.Match;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends MongoRepository<Match, String> {
    List<Match> findByStudentId(String studentId);
    List<Match> findByProviderId(String providerId);
    Optional<Match> findByIdAndStudentId(String matchId, String studentId);
}