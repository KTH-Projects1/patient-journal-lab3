package se.kth.lab2.dto;

import java.time.LocalDate;


public class JournalEntryDTO {

    public Long id;
    public LocalDate date;
    public String time;
    public String content;
    public Long patientId;
    public String doctorName;
    public String patientName;

    public JournalEntryDTO() {
    }
}