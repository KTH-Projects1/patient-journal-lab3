package se.kth.lab3.patient_journal_backend_microservices.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import se.kth.lab3.patient_journal_backend_microservices.config.TestControllerAdvice;
import se.kth.lab3.patient_journal_backend_microservices.dto.PatientDTO;
import se.kth.lab3.patient_journal_backend_microservices.service.PatientService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@Import(TestControllerAdvice.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PatientService patientService;

    private PatientDTO testPatientDTO;

    @BeforeEach
    void setUp() {
        testPatientDTO = new PatientDTO(
                1L,
                "Anna",
                "Andersson",
                "19900101-1234",
                LocalDate.of(1990, 1, 1),
                "anna@example.com",
                "0701234567",
                "Testgatan 1"
        );
    }

    @Test
    void testCreatePatient_Success() throws Exception {
        // Arrange
        when(patientService.createPatient(any(PatientDTO.class))).thenReturn(testPatientDTO);

        // Act & Assert
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPatientDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Anna"))
                .andExpect(jsonPath("$.lastName").value("Andersson"))
                .andExpect(jsonPath("$.personalNumber").value("19900101-1234"))
                .andExpect(jsonPath("$.email").value("anna@example.com"));

        verify(patientService).createPatient(any(PatientDTO.class));
    }

    @Test
    void testCreatePatient_InvalidData_ReturnsBadRequest() throws Exception {
        // Arrange - Patient utan firstName
        PatientDTO invalidPatient = new PatientDTO(
                null, null, "Andersson", "19900101-1234",
                LocalDate.of(1990, 1, 1), "anna@example.com",
                "0701234567", "Testgatan 1"
        );

        // Act & Assert
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest());

        verify(patientService, never()).createPatient(any(PatientDTO.class));
    }

    @Test
    void testGetPatientById_Success() throws Exception {
        // Arrange
        when(patientService.getPatientById(1L)).thenReturn(testPatientDTO);

        // Act & Assert
        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Anna"))
                .andExpect(jsonPath("$.lastName").value("Andersson"));

        verify(patientService).getPatientById(1L);
    }

    @Test
    void testGetPatientById_NotFound() throws Exception {
        // Arrange
        when(patientService.getPatientById(999L))
                .thenThrow(new RuntimeException("Patient med ID 999 finns inte"));

        // Act & Assert
        mockMvc.perform(get("/api/patients/999"))
                .andExpect(status().isNotFound());

        verify(patientService).getPatientById(999L);
    }

    @Test
    void testGetAllPatients_Success() throws Exception {
        // Arrange
        PatientDTO patient2 = new PatientDTO(
                2L, "Erik", "Eriksson", "19850505-5678",
                LocalDate.of(1985, 5, 5), "erik@example.com",
                "0709876543", "Eriksgatan 2"
        );

        List<PatientDTO> patients = Arrays.asList(testPatientDTO, patient2);
        when(patientService.getAllPatients()).thenReturn(patients);

        // Act & Assert
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("Anna"))
                .andExpect(jsonPath("$[1].firstName").value("Erik"));

        verify(patientService).getAllPatients();
    }

    @Test
    void testGetAllPatients_EmptyList() throws Exception {
        // Arrange
        when(patientService.getAllPatients()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(patientService).getAllPatients();
    }

    @Test
    void testUpdatePatient_Success() throws Exception {
        // Arrange
        PatientDTO updatedPatient = new PatientDTO(
                1L, "Anna", "Andersson-Berg", "19900101-1234",
                LocalDate.of(1990, 1, 1), "anna.berg@example.com",
                "0701234567", "Nya gatan 5"
        );

        when(patientService.updatePatient(eq(1L), any(PatientDTO.class)))
                .thenReturn(updatedPatient);

        // Act & Assert
        mockMvc.perform(put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPatient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Andersson-Berg"))
                .andExpect(jsonPath("$.email").value("anna.berg@example.com"))
                .andExpect(jsonPath("$.address").value("Nya gatan 5"));

        verify(patientService).updatePatient(eq(1L), any(PatientDTO.class));
    }

    @Test
    void testUpdatePatient_NotFound() throws Exception {
        // Arrange
        when(patientService.updatePatient(eq(999L), any(PatientDTO.class)))
                .thenThrow(new RuntimeException("Patient med ID 999 finns inte"));

        // Act & Assert
        mockMvc.perform(put("/api/patients/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPatientDTO)))
                .andExpect(status().isNotFound());  // Ändrat från isInternalServerError

        verify(patientService).updatePatient(eq(999L), any(PatientDTO.class));
    }

    @Test
    void testDeletePatient_Success() throws Exception {
        // Arrange
        doNothing().when(patientService).deletePatient(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isNoContent());

        verify(patientService).deletePatient(1L);
    }

    @Test
    void testDeletePatient_NotFound() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Patient med ID 999 finns inte"))
                .when(patientService).deletePatient(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/patients/999"))
                .andExpect(status().isNotFound());

        verify(patientService).deletePatient(999L);
    }
}