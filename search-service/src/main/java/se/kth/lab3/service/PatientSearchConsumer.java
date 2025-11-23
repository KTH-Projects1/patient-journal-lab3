package se.kth.lab3.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import io.smallrye.mutiny.Uni;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import se.kth.lab3.dto.PatientDTO;
import se.kth.lab3.dto.JournalEntryDTO;
import se.kth.lab3.entity.PatientSearchEntry;
import se.kth.lab3.entity.JournalSearchEntry;

@ApplicationScoped
public class PatientSearchConsumer {

    @Incoming("patient-events")
    @WithTransaction
    public Uni<Void> consumePatientEvent(Message<String> message) {
        String json = message.getPayload();
        System.out.println("=== Kafka: Mottog RAW: " + json);

        return Uni.createFrom().item(() -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                        JsonNode node = mapper.readTree(json);

                        PatientDTO patient = new PatientDTO();
                        patient.id = node.get("id").asLong();
                        patient.firstName = node.get("firstName").asText();
                        patient.lastName = node.get("lastName").asText();
                        patient.personalNumber = node.get("personalNumber").asText();
                        patient.email = node.has("email") ? node.get("email").asText() : null;
                        patient.phoneNumber = node.has("phoneNumber") ? node.get("phoneNumber").asText() : null;
                        patient.address = node.has("address") ? node.get("address").asText() : null;

                        if (node.has("dateOfBirth") && node.get("dateOfBirth").isArray()) {
                            JsonNode dateArray = node.get("dateOfBirth");
                            patient.dateOfBirth = String.format("%d-%02d-%02d",
                                    dateArray.get(0).asInt(),
                                    dateArray.get(1).asInt(),
                                    dateArray.get(2).asInt());
                        }

                        System.out.println("=== Kafka: Patient parsed: " + patient.firstName);
                        return patient;
                    } catch (Exception e) {
                        System.err.println("=== Kafka: FEL: " + e.getMessage());
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                })
                .onItem().transformToUni(patient ->
                        PatientSearchEntry.addOrUpdateReactive(patient)
                                .onItem().invoke(() -> System.out.println("=== Kafka: Patient saved OK!"))
                )
                .onItem().transformToUni(v ->
                        Uni.createFrom().completionStage(message.ack())
                );
    }

    @Incoming("journal-events")
    @WithTransaction
    public Uni<Void> consumeJournalEvent(Message<String> message) {
        String json = message.getPayload();

        return Uni.createFrom().item(() -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        JournalEntryDTO journalEntry = mapper.readValue(json, JournalEntryDTO.class);

                        System.out.println("=== Kafka: Mottog journal för patientId: " + journalEntry.patientId);
                        return journalEntry;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .onItem().transformToUni(journalEntry ->
                        JournalSearchEntry.addOrUpdateReactive(journalEntry)
                                .onItem().transformToUni(v -> {
                                    Uni<Void> result = Uni.createFrom().voidItem();

                                    if (journalEntry.patientId != null && journalEntry.note != null) {
                                        result = result.onItem().transformToUni(x ->
                                                PatientSearchEntry.appendJournalContentReactive(journalEntry.patientId, journalEntry.note)
                                        );
                                    }

                                    if (journalEntry.patientId != null && journalEntry.diagnosis != null && !journalEntry.diagnosis.isEmpty()) {
                                        result = result.onItem().transformToUni(x ->
                                                PatientSearchEntry.appendConditionReactive(journalEntry.patientId, journalEntry.diagnosis)
                                        );
                                    }

                                    return result.onItem().invoke(() ->
                                            System.out.println("=== Kafka: Journal OK!")
                                    );
                                })
                )
                .onItem().transformToUni(v ->
                        Uni.createFrom().completionStage(message.ack())
                );
    }
}