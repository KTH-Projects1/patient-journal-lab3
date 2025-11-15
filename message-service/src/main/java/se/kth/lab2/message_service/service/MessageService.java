package se.kth.lab2.message_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.kth.lab2.message_service.dto.MessageDTO;
import se.kth.lab2.message_service.entity.Message;
import se.kth.lab2.message_service.repository.MessageRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageDTO sendMessage(MessageDTO messageDTO) {
        Message message = convertToEntity(messageDTO);

        if (message.getConversationId() == null) {
            Message savedMessage = messageRepository.save(message);
            savedMessage.setConversationId(savedMessage.getId());
            return convertToDTO(messageRepository.save(savedMessage));
        } else {
            Message savedMessage = messageRepository.save(message);
            return convertToDTO(savedMessage);
        }
    }

    public List<MessageDTO> getInboxForUser(String recipientId) {
        return messageRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MessageDTO> getConversation(Long conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MessageDTO markMessageAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Meddelande med ID " + messageId + " finns inte"));
        message.setRead(true);
        Message updatedMessage = messageRepository.save(message);
        return convertToDTO(updatedMessage);
    }

    private MessageDTO convertToDTO(Message entity) {
        return new MessageDTO(
                entity.getId(),
                entity.getSenderId(),
                entity.getRecipientId(),
                entity.getSubject(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.isRead(),
                entity.getConversationId()
        );
    }

    private Message convertToEntity(MessageDTO dto) {
        return new Message(
                dto.getId(),
                dto.getSenderId(),
                dto.getRecipientId(),
                dto.getSubject(),
                dto.getContent(),
                dto.getCreatedAt(),
                dto.isRead(),
                dto.getConversationId()
        );
    }


}