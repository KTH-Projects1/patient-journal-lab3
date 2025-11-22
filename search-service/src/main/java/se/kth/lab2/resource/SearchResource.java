package se.kth.lab2.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import se.kth.lab2.entity.PatientSearchEntry;
import se.kth.lab2.entity.JournalSearchEntry;

import java.util.List;

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
                "patientSearchData LIKE ?1 OR journalSearchData LIKE ?1 OR conditionsData LIKE ?1",
                likeQuery
        );
    }

    @GET
    @Path("/patients/by-name")
    public List<PatientSearchEntry> searchPatientsByName(
            @QueryParam("firstName") String firstName,
            @QueryParam("lastName") String lastName) {

        if ((firstName == null || firstName.trim().isEmpty()) &&
                (lastName == null || lastName.trim().isEmpty())) {
            return PatientSearchEntry.listAll();
        }

        StringBuilder query = new StringBuilder();
        if (firstName != null && !firstName.trim().isEmpty()) {
            query.append("LOWER(firstName) LIKE :firstName");
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            if (query.length() > 0) {
                query.append(" AND ");
            }
            query.append("LOWER(lastName) LIKE :lastName");
        }

        io.quarkus.panache.common.Parameters params = new io.quarkus.panache.common.Parameters();
        if (firstName != null && !firstName.trim().isEmpty()) {
            params = params.and("firstName", "%" + firstName.toLowerCase() + "%");
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            params = params.and("lastName", "%" + lastName.toLowerCase() + "%");
        }

        return PatientSearchEntry.list(query.toString(), params);
    }

    @GET
    @Path("/patients/by-condition")
    public List<PatientSearchEntry> searchPatientsByCondition(@QueryParam("condition") String condition) {
        if (condition == null || condition.trim().isEmpty()) {
            return List.of();
        }

        String likeQuery = "%" + condition.toLowerCase() + "%";
        return PatientSearchEntry.list("conditionsData LIKE ?1", likeQuery);
    }

    @GET
    @Path("/patients/by-personal-number")
    public List<PatientSearchEntry> searchPatientsByPersonalNumber(@QueryParam("personalNumber") String personalNumber) {
        if (personalNumber == null || personalNumber.trim().isEmpty()) {
            return List.of();
        }

        return PatientSearchEntry.list("personalNumber LIKE ?1", "%" + personalNumber + "%");
    }

    @GET
    @Path("/journals")
    public List<JournalSearchEntry> searchJournals(@QueryParam("query") String query) {
        if (query == null || query.trim().isEmpty()) {
            return JournalSearchEntry.listAll();
        }

        String likeQuery = "%" + query.toLowerCase() + "%";
        return JournalSearchEntry.list("searchContent LIKE ?1", likeQuery);
    }

    @GET
    @Path("/journals/by-patient")
    public List<JournalSearchEntry> searchJournalsByPatient(@QueryParam("patientId") Long patientId) {
        if (patientId == null) {
            return List.of();
        }

        return JournalSearchEntry.list("originalPatientId", patientId);
    }

    @GET
    @Path("/health")
    public String health() {
        return "Search service is running with Kafka";
    }
}