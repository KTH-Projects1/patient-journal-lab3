package se.kth.lab3.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import se.kth.lab3.dto.PatientDTO;

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

    public static Uni<Void> addOrUpdateReactive(PatientDTO dto) {
        return PatientSearchEntry.<PatientSearchEntry>find("originalPatientId", dto.id)
                .firstResult()
                .onItem().transformToUni(result -> {
                    PatientSearchEntry entry = result;
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

                    StringBuilder searchData = new StringBuilder();
                    appendIfNotNull(searchData, dto.firstName);
                    appendIfNotNull(searchData, dto.lastName);
                    appendIfNotNull(searchData, dto.personalNumber);
                    appendIfNotNull(searchData, dto.email);
                    appendIfNotNull(searchData, dto.phoneNumber);
                    appendIfNotNull(searchData, dto.address);

                    entry.patientSearchData = searchData.toString().toLowerCase();

                    System.out.println("Uppdaterade patient search entry för: " + dto.firstName + " " + dto.lastName);

                    return entry.persistAndFlush().replaceWithVoid();
                });
    }

    public static Uni<Void> appendJournalContentReactive(Long patientId, String content) {
        return PatientSearchEntry.<PatientSearchEntry>find("originalPatientId", patientId)
                .firstResult()
                .onItem().transformToUni(entry -> {
                    if (entry != null) {
                        if (entry.journalSearchData == null) {
                            entry.journalSearchData = "";
                        }

                        String newContentLower = content.toLowerCase();
                        if (!entry.journalSearchData.contains(newContentLower)) {
                            entry.journalSearchData = entry.journalSearchData + " " + newContentLower;
                            System.out.println("Lade till journalinnehåll för patient: " + patientId);
                            return entry.persistAndFlush().replaceWithVoid();
                        }
                    }
                    return Uni.createFrom().voidItem();
                });
    }

    public static Uni<Void> appendConditionReactive(Long patientId, String condition) {
        return PatientSearchEntry.<PatientSearchEntry>find("originalPatientId", patientId)
                .firstResult()
                .onItem().transformToUni(entry -> {
                    if (entry != null) {
                        if (entry.conditionsData == null) {
                            entry.conditionsData = "";
                        }

                        String conditionLower = condition.toLowerCase();
                        if (!entry.conditionsData.contains(conditionLower)) {
                            entry.conditionsData = entry.conditionsData + " " + conditionLower;
                            System.out.println("Lade till condition för patient: " + patientId);
                            return entry.persistAndFlush().replaceWithVoid();
                        }
                    }
                    return Uni.createFrom().voidItem();
                });
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