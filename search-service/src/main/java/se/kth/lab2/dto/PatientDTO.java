package se.kth.lab2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientDTO {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("firstName")
    public String firstName;

    @JsonProperty("lastName")
    public String lastName;

    @JsonProperty("personalNumber")
    public String personalNumber;

    @JsonProperty("email")
    public String email;

    @JsonProperty("dateOfBirth")
    public String dateOfBirth;

    @JsonProperty("phoneNumber")
    public String phoneNumber;

    @JsonProperty("address")
    public String address;

    public PatientDTO() {
    }
}