package se.kth.lab3.dto;

import java.time.LocalDate;

public class EncounterDTO {

    public Long id;
    public Long originalDoctorId;
    public Long originalPatientId;

    public String doctorName;
    public String patientName;
    public LocalDate encounterDate;

    public EncounterDTO() {
    }

}