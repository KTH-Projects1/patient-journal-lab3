package se.kth.lab3.message_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.kth.lab3.message_service.dto.MessageDTO;
import se.kth.lab3.message_service.entity.Message;
import se.kth.lab3.message_service.repository.MessageRepository;
import se.kth.lab3.message_service.service.MessageService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    private Message testMessage;
    private MessageDTO testMessageDTO;

    @BeforeEach
    void setUp() {
        testMessage = new Message(
                1L,
                "doctor1",
                "doctor2",
                "Patientkonsultation",
                "Kan du titta på patient 123?",
                LocalDateTime.now(),
                false,
                1L
        );

        testMessageDTO = new MessageDTO(
                1L,
                "doctor1",
                "doctor2",
                "Patientkonsultation",
                "Kan du titta på patient 123?",
                LocalDateTime.now(),
                false,
                1L
        );
    }

    @Test
    void testSendMessage_NewConversation_Success() {
        MessageDTO newMessageDTO = new MessageDTO(
                null, "doctor1", "doctor2", "Ny konsultation",
                "Hej!", null, false, null
        );

        Message savedMessage = new Message(
                1L, "doctor1", "doctor2", "Ny konsultation",
                "Hej!", LocalDateTime.now(), false, null
        );

        Message finalMessage = new Message(
                1L, "doctor1", "doctor2", "Ny konsultation",
                "Hej!", LocalDateTime.now(), false, 1L
        );

        when(messageRepository.save(any(Message.class)))
                .thenReturn(savedMessage)
                .thenReturn(finalMessage);

        MessageDTO result = messageService.sendMessage(newMessageDTO);

        assertNotNull(result);
        assertEquals("doctor1", result.getSenderId());
        assertEquals("doctor2", result.getRecipientId());
        assertEquals("Ny konsultation", result.getSubject());
        assertNotNull(result.getConversationId());
        assertFalse(result.isRead());

        verify(messageRepository, times(2)).save(any(Message.class));
    }

    @Test
    void testSendMessage_ExistingConversation_Success() {
        MessageDTO replyDTO = new MessageDTO(
                null, "doctor2", "doctor1", "Re: Patientkonsultation",
                "Ja, jag tittar på det!", null, false, 1L
        );

        Message savedMessage = new Message(
                2L, "doctor2", "doctor1", "Re: Patientkonsultation",
                "Ja, jag tittar på det!", LocalDateTime.now(), false, 1L
        );

        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        MessageDTO result = messageService.sendMessage(replyDTO);

        assertNotNull(result);
        assertEquals(1L, result.getConversationId());
        assertEquals("doctor2", result.getSenderId());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void testGetInboxForUser_Success() {
        Message message2 = new Message(
                2L, "doctor3", "doctor2", "Akut fall",
                "Behöver hjälp nu!", LocalDateTime.now(), false, 2L
        );

        when(messageRepository.findByRecipientIdOrderByCreatedAtDesc("doctor2"))
                .thenReturn(Arrays.asList(testMessage, message2));

        List<MessageDTO> inbox = messageService.getInboxForUser("doctor2");

        assertNotNull(inbox);
        assertEquals(2, inbox.size());
        assertEquals("doctor1", inbox.get(0).getSenderId());
        assertEquals("doctor3", inbox.get(1).getSenderId());

        verify(messageRepository).findByRecipientIdOrderByCreatedAtDesc("doctor2");
    }

    @Test
    void testGetInboxForUser_EmptyInbox() {
        when(messageRepository.findByRecipientIdOrderByCreatedAtDesc("doctor5"))
                .thenReturn(Arrays.asList());

        List<MessageDTO> inbox = messageService.getInboxForUser("doctor5");

        assertNotNull(inbox);
        assertEquals(0, inbox.size());

        verify(messageRepository).findByRecipientIdOrderByCreatedAtDesc("doctor5");
    }

    @Test
    void testGetConversation_Success() {
        Message reply = new Message(
                2L, "doctor2", "doctor1", "Re: Patientkonsultation",
                "Ja, jag tittar!", LocalDateTime.now().plusMinutes(5), false, 1L
        );

        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(1L))
                .thenReturn(Arrays.asList(testMessage, reply));

        List<MessageDTO> conversation = messageService.getConversation(1L);

        assertNotNull(conversation);
        assertEquals(2, conversation.size());
        assertEquals("doctor1", conversation.get(0).getSenderId());
        assertEquals("doctor2", conversation.get(1).getSenderId());
        assertEquals(1L, conversation.get(0).getConversationId());

        verify(messageRepository).findByConversationIdOrderByCreatedAtAsc(1L);
    }

    @Test
    void testMarkMessageAsRead_Success() {
        when(messageRepository.findById(1L)).thenReturn(Optional.of(testMessage));

        Message updatedMessage = new Message(
                1L, "doctor1", "doctor2", "Patientkonsultation",
                "Kan du titta på patient 123?", testMessage.getCreatedAt(), true, 1L
        );

        when(messageRepository.save(any(Message.class))).thenReturn(updatedMessage);

        MessageDTO result = messageService.markMessageAsRead(1L);

        assertNotNull(result);
        assertTrue(result.isRead());

        verify(messageRepository).findById(1L);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void testMarkMessageAsRead_NotFound_ThrowsException() {
        when(messageRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            messageService.markMessageAsRead(999L);
        });

        assertTrue(exception.getMessage().contains("finns inte"));
        verify(messageRepository).findById(999L);
        verify(messageRepository, never()).save(any(Message.class));
    }
}