package se.kth.lab3.message_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String senderId;

    @Column(nullable = false)
    private String recipientId;

    @Column(nullable = false)
    private String subject;

    @NotBlank(message = "Innehåll krävs")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isRead = false;

    private Long conversationId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}