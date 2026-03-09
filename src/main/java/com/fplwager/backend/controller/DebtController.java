package com.fplwager.backend.controller;

import com.fplwager.backend.dto.DebtSummary;
import com.fplwager.backend.service.DebtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class DebtController {

    private final DebtService debtService;

    @GetMapping("/{groupId}/debts")
    public ResponseEntity<DebtSummary> getMyDebts(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId) {
        return ResponseEntity.ok(debtService.getMyDebtSummary(userDetails.getUsername(), groupId));
    }
}