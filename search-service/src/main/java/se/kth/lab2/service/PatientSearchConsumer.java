package se.kth.lab2.service;

import import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import se.kth.lab2.dto.PatientDTO;
import se.kth.lab2.entity.PatientSearchEntry;
import se.kth.lab2.dto.JournalEntryDTO;
import se.kth.lab2.entity.JournalSearchEntry;

@ApplicationScoped
public class PatientSearchConsumer {

    @Incoming("patient-events")
    @Blocking
    @Transactional
    public void consumePatientEvent(PatientDTO patient) {
        System.out.println("=== Kafka: Mottog patient-händelse: " + patient.firstName + " " + patient.lastName);
        try {
            PatientSearchEntry.addOrUpdate(patient);
            System.out.println("=== Kafka: Patient-händelse bearbetad framgångsrikt");
        } catch (Exception e) {
            System.err.println("=== Kafka: Fel vid bearbetning av patient-händelse: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Incoming("journal-events")
    @Blocking
    @Transactional
    public void consumeJournalEvent(JournalEntryDTO journalEntry) {
        System.out.println("=== Kafka: Mottog journal-händelse för patientId: " + journalEntry.patientId);
        try {
            // Update journal search entry
            JournalSearchEntry.addOrUpdate(journalEntry);

            // Append to patient's searchable journal content
            if (journalEntry.patientId != null && journalEntry.note != null) {
                PatientSearchEntry.appendJournalContent(journalEntry.patientId, journalEntry.note);
            }

            // Append diagnosis as condition
            if (journalEntry.patientId != null && journalEntry.diagnosis != null && !journalEntry.diagnosis.isEmpty()) {
                PatientSearchEntry.appendCondition(journalEntry.patientId, journalEntry.diagnosis);
            }

            System.out.println("=== Kafka: Journal-händelse bearbetad framgångsrikt");
        } catch (Exception e) {
            System.err.println("=== Kafka: Fel vid bearbetning av journal-händelse: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


}