package com.nursematch.payment.controller;

import com.nursematch.payment.dto.DepositRequest;
import com.nursematch.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/deposit")
    public ResponseEntity<String> payDeposit(
            @RequestBody @Valid DepositRequest req,
            Authentication auth) {
        paymentService.payDeposit(auth.getName(), req.getMatchId());
        return ResponseEntity.ok("Deposit paid — match unlocked");
    }
}