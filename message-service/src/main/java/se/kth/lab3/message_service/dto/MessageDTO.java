package se.kth.lab3.message_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private Long id;

    @NotBlank(message = "Avsändar-ID krävs")
    private String senderId;

    @NotBlank(message = "Mottagar-ID krävs")
    private String recipientId;

    @NotBlank(message = "Ämne krävs")
    private String subject;

    @NotBlank(message = "Innehåll krävs")
    private String content;

    private LocalDateTime createdAt;

    private boolean isRead;

    private Long conversationId;
}