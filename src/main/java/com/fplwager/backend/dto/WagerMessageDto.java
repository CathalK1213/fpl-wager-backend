package com.fplwager.backend.dto;

import com.fplwager.backend.model.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WagerMessageDto {
    private Long id;
    private Long wagerId;
    private String senderUsername;
    private String content;
    private String gifUrl;
    private String videoUrl;
    private MessageType messageType;
    private LocalDateTime sentAt;
}