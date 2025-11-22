package se.kth.lab2.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import se.kth.lab2.dto.JournalEntryDTO;

@Entity
public class JournalSearchEntry extends PanacheEntity {

    @Column(unique = true)
    public Long originalJournalId;

    public Long originalPatientId;
    public String note;
    public String diagnosis;
    public String treatment;
    public String createdAt;

    @Column(columnDefinition = "TEXT")
    public String searchContent;

    public static void addOrUpdate(JournalEntryDTO dto) {
        JournalSearchEntry entry = find("originalJournalId", dto.id).firstResult();
        if (entry == null) {
            entry = new JournalSearchEntry();
            entry.originalJournalId = dto.id;
        }

        entry.originalPatientId = dto.patientId;
        entry.note = dto.note;
        entry.diagnosis = dto.diagnosis;
        entry.treatment = dto.treatment;
        entry.createdAt = dto.createdAt;

        // Build searchable content
        StringBuilder searchContent = new StringBuilder();
        appendIfNotNull(searchContent, dto.note);
        appendIfNotNull(searchContent, dto.diagnosis);
        appendIfNotNull(searchContent, dto.treatment);

        entry.searchContent = searchContent.toString().toLowerCase();

        entry.persist();

        System.out.println("Uppdaterade journal search entry för patient: " + dto.patientId);
    }

    private static void appendIfNotNull(StringBuilder sb, String value) {
        if (value != null && !value.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(value);
        }
    }
}


