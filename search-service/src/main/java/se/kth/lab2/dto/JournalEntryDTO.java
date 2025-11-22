package se.kth.lab2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JournalEntryDTO {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("patientId")
    public Long patientId;

    @JsonProperty("note")
    public String note;

    @JsonProperty("createdAt")
    public String createdAt;

    @JsonProperty("diagnosis")
    public String diagnosis;

    @JsonProperty("treatment")
    public String treatment;

    public JournalEntryDTO() {
    }
}