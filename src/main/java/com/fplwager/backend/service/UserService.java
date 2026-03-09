package com.fplwager.backend.service;

import com.fplwager.backend.dto.FplEntryResponse;
import com.fplwager.backend.dto.FplTeamRequest;
import com.fplwager.backend.model.User;
import com.fplwager.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FplSyncService fplSyncService;

    @Transactional
    public FplEntryResponse linkFplTeam(String username, FplTeamRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setFplTeamId(request.getFplTeamId());
        userRepository.save(user);

        return fplSyncService.syncUser(user);
    }
}