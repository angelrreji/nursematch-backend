package com.nursematch.provider.repository;

import com.nursematch.provider.model.ProviderProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProviderProfileRepository extends MongoRepository<ProviderProfile, String> {

    Optional<ProviderProfile> findByUserId(String userId);

    boolean existsByUserId(String userId);

    // Core match query — hits compound index
    List<ProviderProfile> findBySpecialtiesContainingAndAcceptingStudentsTrueAndState(
            String specialty, String state
    );
}