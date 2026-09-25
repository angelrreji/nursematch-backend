package com.nursematch.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepositRequest {

    @NotBlank
    private String matchId;
}