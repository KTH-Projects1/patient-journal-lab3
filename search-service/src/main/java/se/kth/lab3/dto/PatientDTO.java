package se.kth.lab3.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public String dateOfBirth;

    @JsonProperty("phoneNumber")
    public String phoneNumber;

    @JsonProperty("address")
    public String address;

    public PatientDTO() {
    }
}