package com.fplwager.backend.dto;

import com.fplwager.backend.model.MessageType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequest {
    @NotNull
    private Long groupId;
    @NotNull
    private Integer gameweek;
    private String content;
    private String gifUrl;
    @NotNull
    private MessageType messageType;
}