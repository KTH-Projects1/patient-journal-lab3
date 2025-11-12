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

    public Long originalDoctorId;
    public String doctorName;

    @Column(columnDefinition = "TEXT")
    public String patientSearchData;

    @Column(columnDefinition = "TEXT")
    public String journalSearchData;

    public static void addOrUpdate(PatientDTO dto) {
        PatientSearchEntry entry = find("originalPatientId", dto.id).firstResult();
        if (entry == null) {
            entry = new PatientSearchEntry();
            entry.originalPatientId = dto.id;
            entry.journalSearchData = "";
        }

        entry.firstName = dto.firstName;
        entry.lastName = dto.lastName;
        entry.originalDoctorId = dto.originalDoctorId;
        entry.doctorName = dto.doctorName;

        entry.patientSearchData = (dto.firstName + " " + dto.lastName + " " + dto.personalNumber + " " + dto.email + " " + dto.doctorName).toLowerCase();

        entry.persist();
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
            }
        } else {
            System.err.println("Kunde inte hitta patient " + patientId + " för att lägga till journaltext.");
        }
    }
}