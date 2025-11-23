package se.kth.lab3.message_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.kth.lab3.message_service.entity.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRecipientIdOrderByCreatedAtDesc(String recipientId);

    List<Message> findBySenderIdOrderByCreatedAtDesc(String senderId);

    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);
}