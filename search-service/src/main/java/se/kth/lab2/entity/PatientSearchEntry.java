package se.kth.lab2.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import se.kth.lab2.dto.PatientDTO;

@Entity
public class PatientSearchEntry extends PanacheEntity {

    @Column(unique = true)
    public Long originalPatientId;

    public String firstName;
    public String lastName;
    public String personalNumber;
    public String email;
    public String phoneNumber;
    public String address;
    public String dateOfBirth;

    @Column(columnDefinition = "TEXT")
    public String patientSearchData;

    @Column(columnDefinition = "TEXT")
    public String journalSearchData;

    @Column(columnDefinition = "TEXT")
    public String conditionsData;

    public static void addOrUpdate(PatientDTO dto) {
        PatientSearchEntry entry = find("originalPatientId", dto.id).firstResult();
        if (entry == null) {
            entry = new PatientSearchEntry();
            entry.originalPatientId = dto.id;
            entry.journalSearchData = "";
            entry.conditionsData = "";
        }

        entry.firstName = dto.firstName;
        entry.lastName = dto.lastName;
        entry.personalNumber = dto.personalNumber;
        entry.email = dto.email;
        entry.phoneNumber = dto.phoneNumber;
        entry.address = dto.address;
        entry.dateOfBirth = dto.dateOfBirth;

        // Build searchable data string
        StringBuilder searchData = new StringBuilder();
        appendIfNotNull(searchData, dto.firstName);
        appendIfNotNull(searchData, dto.lastName);
        appendIfNotNull(searchData, dto.personalNumber);
        appendIfNotNull(searchData, dto.email);
        appendIfNotNull(searchData, dto.phoneNumber);
        appendIfNotNull(searchData, dto.address);

        entry.patientSearchData = searchData.toString().toLowerCase();

        entry.persist();

        System.out.println("Uppdaterade patient search entry för: " + dto.firstName + " " + dto.lastName);
    }

    public static void appendJournalContent(Long patientId, String content) {
        PatientSearchEntry entry = find("originalPatientId", patientId).firstResult();
        if (entry != null) {
            if (entry.journalSearchData == null) {
                entry.journalSearchData = "";
            }

            String newContentLower = content.toLowerCase();
            if (!entry.journalSearchData.contains(newContentLower)) {
                entry.journalSearchData = entry.journalSearchData + " " + newContentLower;
                entry.persist();
                System.out.println("Lade till journalinnehåll för patient: " + patientId);
            }
        } else {
            System.err.println("Kunde inte hitta patient " + patientId + " för att lägga till journaltext.");
        }
    }

    public static void appendCondition(Long patientId, String condition) {
        PatientSearchEntry entry = find("originalPatientId", patientId).firstResult();
        if (entry != null) {
            if (entry.conditionsData == null) {
                entry.conditionsData = "";
            }

            String conditionLower = condition.toLowerCase();
            if (!entry.conditionsData.contains(conditionLower)) {
                entry.conditionsData = entry.conditionsData + " " + conditionLower;
                entry.persist();
                System.out.println("Lade till condition för patient: " + patientId);
            }
        }
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