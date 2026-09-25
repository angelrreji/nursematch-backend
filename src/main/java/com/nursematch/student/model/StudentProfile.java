package com.nursematch.student.model;

import com.nursematch.rotation.model.RotationRequest;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "student_profiles")
@Data
public class StudentProfile {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;        // FK reference to User._id

    private String university;
    private String program;
    private LocalDate graduationDate;

    private List<RotationRequest> rotationRequests = new ArrayList<>();
}