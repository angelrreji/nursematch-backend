package com.nursematch.payment.service;

import com.nursematch.exception.ResourceNotFoundException;
import com.nursematch.match.model.Match;
import com.nursematch.match.model.MatchStatus;
import com.nursematch.match.repository.MatchRepository;
import com.nursematch.user.model.User;
import com.nursematch.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    public void payDeposit(String email, String matchId) {

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Match match = matchRepository.findByIdAndStudentId(matchId, student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        match.setDepositPaid(true);
        match.setStatus(MatchStatus.DEPOSIT_PAID);

        matchRepository.save(match);
    }
}