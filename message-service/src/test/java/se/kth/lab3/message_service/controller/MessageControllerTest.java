package se.kth.lab3.message_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;  // NY IMPORT
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import se.kth.lab3.message_service.config.TestControllerAdvice;  // NY IMPORT
import se.kth.lab3.message_service.dto.MessageDTO;
import se.kth.lab3.message_service.service.MessageService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
@Import(TestControllerAdvice.class)  // LÄGG TILL DENNA RAD
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;

    private MessageDTO testMessageDTO;

    @BeforeEach
    void setUp() {
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
    void testSendMessage_Success() throws Exception {
        when(messageService.sendMessage(any(MessageDTO.class))).thenReturn(testMessageDTO);

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMessageDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.senderId").value("doctor1"))
                .andExpect(jsonPath("$.recipientId").value("doctor2"))
                .andExpect(jsonPath("$.subject").value("Patientkonsultation"))
                .andExpect(jsonPath("$.content").value("Kan du titta på patient 123?"))
                .andExpect(jsonPath("$.read").value(false))
                .andExpect(jsonPath("$.conversationId").value(1));

        verify(messageService).sendMessage(any(MessageDTO.class));
    }

    @Test
    void testSendMessage_InvalidData_ReturnsBadRequest() throws Exception {
        MessageDTO invalidMessage = new MessageDTO(
                null, null, "doctor2", "Subject", "Content",
                LocalDateTime.now(), false, null
        );

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMessage)))
                .andExpect(status().isBadRequest());

        verify(messageService, never()).sendMessage(any(MessageDTO.class));
    }

    @Test
    void testGetInbox_Success() throws Exception {
        MessageDTO message2 = new MessageDTO(
                2L, "doctor3", "doctor2", "Akut fall",
                "Behöver hjälp!", LocalDateTime.now(), false, 2L
        );

        List<MessageDTO> inbox = Arrays.asList(testMessageDTO, message2);
        when(messageService.getInboxForUser("doctor2")).thenReturn(inbox);

        mockMvc.perform(get("/api/messages/inbox/doctor2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].senderId").value("doctor1"))
                .andExpect(jsonPath("$[1].senderId").value("doctor3"));

        verify(messageService).getInboxForUser("doctor2");
    }

    @Test
    void testGetInbox_EmptyInbox() throws Exception {
        when(messageService.getInboxForUser("doctor5")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/messages/inbox/doctor5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(messageService).getInboxForUser("doctor5");
    }

    @Test
    void testGetConversation_Success() throws Exception {
        MessageDTO reply = new MessageDTO(
                2L, "doctor2", "doctor1", "Re: Patientkonsultation",
                "Ja!", LocalDateTime.now(), false, 1L
        );

        List<MessageDTO> conversation = Arrays.asList(testMessageDTO, reply);
        when(messageService.getConversation(1L)).thenReturn(conversation);

        mockMvc.perform(get("/api/messages/conversation/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].conversationId").value(1))
                .andExpect(jsonPath("$[1].conversationId").value(1));

        verify(messageService).getConversation(1L);
    }

    @Test
    void testMarkAsRead_Success() throws Exception {
        MessageDTO readMessage = new MessageDTO(
                1L, "doctor1", "doctor2", "Patientkonsultation",
                "Kan du titta på patient 123?", LocalDateTime.now(), true, 1L
        );

        when(messageService.markMessageAsRead(1L)).thenReturn(readMessage);

        mockMvc.perform(put("/api/messages/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.read").value(true));

        verify(messageService).markMessageAsRead(1L);
    }

    @Test
    void testMarkAsRead_NotFound() throws Exception {
        when(messageService.markMessageAsRead(999L))
                .thenThrow(new RuntimeException("Meddelande med ID 999 finns inte"));

        mockMvc.perform(put("/api/messages/999/read"))
                .andExpect(status().isNotFound());  // Ändrat från isInternalServerError

        verify(messageService).markMessageAsRead(999L);
    }
}