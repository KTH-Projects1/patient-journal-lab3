package se.kth.lab2.message_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.kth.lab2.message_service.dto.MessageDTO;
import se.kth.lab2.message_service.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(@Valid @RequestBody MessageDTO messageDTO) {
        MessageDTO createdMessage = messageService.sendMessage(messageDTO);
        return new ResponseEntity<>(createdMessage, HttpStatus.CREATED);
    }

    @GetMapping("/inbox/{recipientId}")
    public ResponseEntity<List<MessageDTO>> getInbox(@PathVariable String recipientId) {
        List<MessageDTO> inbox = messageService.getInboxForUser(recipientId);
        return ResponseEntity.ok(inbox);
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageDTO>> getConversation(@PathVariable Long conversationId) {
        List<MessageDTO> conversation = messageService.getConversation(conversationId);
        return ResponseEntity.ok(conversation);
    }

    @PutMapping("/{messageId}/read")
    public ResponseEntity<MessageDTO> markAsRead(@PathVariable Long messageId) {
        MessageDTO updatedMessage = messageService.markMessageAsRead(messageId);
        return ResponseEntity.ok(updatedMessage);
    }

    
}