package se.kth.lab3.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Entity;
import se.kth.lab3.dto.EncounterDTO;

import java.time.LocalDate;

@Entity
public class EncounterSearchEntry extends PanacheEntity {

    public Long originalEncounterId;
    public Long originalDoctorId;
    public Long originalPatientId;

    public String doctorName;
    public String patientName;
    public LocalDate encounterDate;

    public static Uni<Void> addOrUpdateReactive(EncounterDTO dto) {
        return EncounterSearchEntry.<EncounterSearchEntry>find("originalEncounterId", dto.id)
                .firstResult()
                .onItem().transformToUni(result -> {
                    EncounterSearchEntry entry = result;
                    if (entry == null) {
                        entry = new EncounterSearchEntry();
                        entry.originalEncounterId = dto.id;
                    }

                    entry.originalDoctorId = dto.originalDoctorId;
                    entry.originalPatientId = dto.originalPatientId;
                    entry.doctorName = dto.doctorName;
                    entry.patientName = dto.patientName;
                    entry.encounterDate = dto.encounterDate;

                    return entry.persistAndFlush().replaceWithVoid();
                });
    }
}