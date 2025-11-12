package se.kth.lab2.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import se.kth.lab2.entity.EncounterSearchEntry;
import se.kth.lab2.entity.PatientSearchEntry;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Path("/api/search")
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {

    @GET
    @Path("/patients")
    public List<PatientSearchEntry> searchPatients(@QueryParam("query") String query) {
        if (query == null || query.trim().isEmpty()) {
            return PatientSearchEntry.listAll();
        }

        String likeQuery = "%" + query.toLowerCase() + "%";

        return PatientSearchEntry.list(
                "patientSearchData LIKE ?1 OR journalSearchData LIKE ?1",
                likeQuery
        );
    }

    @GET
    @Path("/doctors/patients")
    public List<PatientSearchEntry> searchPatientsByDoctor(@QueryParam("doctorId") Long doctorId) {
        if (doctorId == null) {
            return List.of();
        }
        return PatientSearchEntry.list("originalDoctorId", doctorId);
    }

    @GET
    @Path("/doctors/encounters")
    public List<EncounterSearchEntry> searchEncountersByDoctorAndDate(
            @QueryParam("doctorId") Long doctorId,
            @QueryParam("date") String dateString) {

        if (doctorId == null || dateString == null || dateString.trim().isEmpty()) {
            return List.of();
        }

        try {
            LocalDate date = LocalDate.parse(dateString);

            return EncounterSearchEntry.list(
                    "originalDoctorId = :doctorId AND encounterDate = :date",
                    Map.of("doctorId", doctorId, "date", date)
            );
        } catch (DateTimeParseException e) {
            return List.of();
        }
    }
}