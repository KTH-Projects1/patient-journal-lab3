package se.kth.lab3.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import se.kth.lab3.dto.EncounterDTO;
import se.kth.lab3.entity.EncounterSearchEntry;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class EncounterSearchConsumer {

    @Incoming("encounter-events")
    public Uni<Void> consumeEncounter(Message<String> message) {
        String json = message.getPayload();

        return Uni.createFrom().item(() -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        return mapper.readValue(json, EncounterDTO.class);
                    } catch (Exception e) {
                        System.err.println("Kunde inte bearbeta encounter-händelse: " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                })
                .onItem().transformToUni(dto ->
                        EncounterSearchEntry.addOrUpdateReactive(dto)
                )
                .onItem().transformToUni(v ->
                        Uni.createFrom().completionStage(message.ack())
                );
    }
}