package se.kth.lab2.service;

import io.quarkus.hibernate.orm.panache.Panache;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import se.kth.lab2.dto.EncounterDTO;
import se.kth.lab2.entity.EncounterSearchEntry;

@ApplicationScoped
public class EncounterSearchConsumer {

    @Incoming("encounter-events")
    @Transactional
    public void consumeEncounter(EncounterDTO dto) {
        try {
            EncounterSearchEntry.addOrUpdate(dto);
        } catch (Exception e) {
            System.err.println("Kunde inte bearbeta encounter-händelse: " + e.getMessage());
        }
    }
}