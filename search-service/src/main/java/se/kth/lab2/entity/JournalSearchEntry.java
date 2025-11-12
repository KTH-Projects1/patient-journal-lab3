package se.kth.lab2.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import se.kth.lab2.dto.JournalEntryDTO;

import java.time.LocalDate;

@Entity
public class JournalSearchEntry extends PanacheEntity {

    @Column(unique = true)
    public Long originalJournalId;

    public Long originalPatientId;
    public String doctorName;
    public String patientName;
    public LocalDate journalDate;

    @Column(columnDefinition = "TEXT")
    public String content;

    public static void addOrUpdate(JournalEntryDTO dto) {
        JournalSearchEntry entry = find("originalJournalId", dto.id).firstResult();
        if (entry == null) {
            entry = new JournalSearchEntry();
            entry.originalJournalId = dto.id;
        }

        entry.originalPatientId = dto.patientId;
        entry.doctorName = dto.doctorName;
        entry.patientName = dto.patientName;
        entry.journalDate = dto.date;
        entry.content = dto.content;

        entry.persist();
    }
}