package com.fplwager.backend.dto;

import com.fplwager.backend.model.MessageType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendWagerMessageRequest {
    @NotNull
    private Long wagerId;
    private String content;
    private String gifUrl;
    private String videoUrl;
    @NotNull
    private MessageType messageType;
}