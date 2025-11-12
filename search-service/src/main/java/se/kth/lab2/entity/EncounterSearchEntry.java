package se.kth.lab2.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import se.kth.lab2.dto.EncounterDTO;

import java.time.LocalDate;

@Entity
public class EncounterSearchEntry extends PanacheEntity {

    public Long originalEncounterId;
    public Long originalDoctorId;
    public Long originalPatientId;

    public String doctorName;
    public String patientName;
    public LocalDate encounterDate;

    public static void addOrUpdate(EncounterDTO dto) {
        EncounterSearchEntry entry = find("originalEncounterId", dto.id).firstResult();
        if (entry == null) {
            entry = new EncounterSearchEntry();
            entry.originalEncounterId = dto.id;
        }

        entry.originalDoctorId = dto.originalDoctorId;
        entry.originalPatientId = dto.originalPatientId;
        entry.doctorName = dto.doctorName;
        entry.patientName = dto.patientName;
        entry.encounterDate = dto.encounterDate;

        entry.persist();
    }
}