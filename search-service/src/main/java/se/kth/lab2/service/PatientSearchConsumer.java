package se.kth.lab2.service;

import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import se.kth.lab2.dto.PatientDTO;
import se.kth.lab2.entity.PatientSearchEntry;
import se.kth.lab2.dto.JournalEntryDTO;      // NY IMPORT
import se.kth.lab2.entity.JournalSearchEntry;   // NY IMPORT

@ApplicationScoped
public class PatientSearchConsumer {

    @Incoming("patient-events")
    @Blocking
    @Transactional
    public void consumePatientEvent(PatientDTO patient) {
        System.out.println("Mottog patient-händelse: " + patient.firstName);
        PatientSearchEntry.addOrUpdate(patient);
    }

    @Incoming("journal-events")
    @Blocking
    @Transactional
    public void consumeJournalEvent(JournalEntryDTO journalEntry) {
        System.out.println("Mottog journal-händelse för patientId: " + journalEntry.patientId);
        try {
            JournalSearchEntry.addOrUpdate(journalEntry);
            if (journalEntry.patientId != null && journalEntry.content != null) {
                PatientSearchEntry.appendJournalContent(journalEntry.patientId, journalEntry.content);
            }
        } catch (Exception e) {
            System.err.println("Kunde inte bearbeta journal-händelse: " + e.getMessage());
        }
    }
}