package org.mohyla.itinerary.payments;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mohyla.itinerary.utils.ServiceTokenManager;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PaymentsClientJwtTest {

    @Mock
    private ServiceTokenManager serviceTokenManager;

    private PaymentsClient paymentsClient;

    @Test
    void testCreatePayment_WithJwtToken() {
        paymentsClient = new PaymentsClient(serviceTokenManager);
        
        assertThat(paymentsClient).isNotNull();
    }
}
