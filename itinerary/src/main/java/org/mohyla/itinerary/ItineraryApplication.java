package org.mohyla.itinerary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class ItineraryApplication {

    public static void main(String[] args) {

       SpringApplication.run(ItineraryApplication.class, args);

    }

}
