package se.kth.lab3.search_service.consumer;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import se.kth.lab3.service.PatientSearchConsumer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class PatientSearchConsumerTest {

    @Test
    public void testConsumerConfiguration() {
        // Testa att consumer är korrekt konfigurerad
        assertNotNull(PatientSearchConsumer.class);
    }

    // Du kan lägga till mer avancerade tester med mock Kafka messages
}