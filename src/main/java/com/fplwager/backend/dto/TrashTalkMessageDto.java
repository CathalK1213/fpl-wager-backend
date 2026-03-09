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
public class TrashTalkMessageDto {
    private Long id;
    private String senderUsername;
    private Long groupId;
    private Integer gameweek;
    private String content;
    private String gifUrl;
    private MessageType messageType;
    private LocalDateTime sentAt;
}