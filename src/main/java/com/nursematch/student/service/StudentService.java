package com.nursematch.student.service;

import com.nursematch.student.dto.StudentProfileRequest;
import com.nursematch.student.dto.StudentProfileResponse;
import com.nursematch.student.model.StudentProfile;
import com.nursematch.student.repository.StudentProfileRepository;
import com.nursematch.user.model.User;
import com.nursematch.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentProfileRepository profileRepository;
    private final UserRepository userRepository;

    public StudentProfileResponse createProfile(String email, StudentProfileRequest req) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (profileRepository.existsByUserId(user.getId())) {
            throw new RuntimeException("Profile already exists");
        }

        StudentProfile profile = new StudentProfile();
        profile.setUserId(user.getId());
        profile.setUniversity(req.getUniversity());
        profile.setProgram(req.getProgram());
        profile.setGraduationDate(req.getGraduationDate());

        profileRepository.save(profile);

        // mark user profile complete
        user.setProfileComplete(true);
        userRepository.save(user);

        return toResponse(profile);
    }

    public StudentProfileResponse getProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return toResponse(profile);
    }

    private StudentProfileResponse toResponse(StudentProfile profile) {
        return new StudentProfileResponse(
                profile.getId(),
                profile.getUserId(),
                profile.getUniversity(),
                profile.getProgram(),
                profile.getGraduationDate()
        );
    }
}