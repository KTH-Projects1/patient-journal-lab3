package se.kth.lab3.search_service.consumer;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import se.kth.lab3.service.PatientSearchConsumer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class PatientSearchConsumerTest {

    @Inject
    PatientSearchConsumer patientSearchConsumer;

    @Test
    public void testConsumerIsInjectable() {
        assertNotNull(patientSearchConsumer);
    }

    @Test
    public void testConsumerClassExists() {
        assertNotNull(PatientSearchConsumer.class);
    }
}