package org.chatbot;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AutoEnrichmentJob {

    @Inject
    DataEnrichmentService dataEnrichmentService;

    @Scheduled(every = "24h") // Exécute toutes les 24 heures
    void enrichDatabase() {
        dataEnrichmentService.fetchAndEnrichData();
    }
}
