package com.fplwager.backend.controller;

import com.fplwager.backend.dto.FplEntryResponse;
import com.fplwager.backend.dto.FplTeamRequest;
import com.fplwager.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/fpl-team")
    public ResponseEntity<FplEntryResponse> linkFplTeam(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody FplTeamRequest request) {
        return ResponseEntity.ok(userService.linkFplTeam(userDetails.getUsername(), request));
    }
}
