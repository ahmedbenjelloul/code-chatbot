package org.chatbot;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@ApplicationScoped
public class DataEnrichmentService {

    @Inject
    EntityManager entityManager;

    @Transactional
    public void fetchAndEnrichData() {
        try {
            URL apiUrl = new URL("https://api.stackexchange.com/2.3/questions?order=desc&sort=activity&tagged=java&site=stackoverflow");
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");

            System.out.println("Fetching data from API...");
            List<String> newQuestions = parseApiResponse(connection.getInputStream());
            System.out.println("Fetched " + newQuestions.size() + " questions.");

            for (String question : newQuestions) {
                persistKnowledge("Programming", question, "Voici une réponse automatique.");
            }

            System.out.println("Data enrichment completed!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void persistKnowledge(String category, String question, String answer) {
        KnowledgeBase kb = new KnowledgeBase(category, question, answer);
        entityManager.persist(kb);
        entityManager.flush();
    }

    private List<String> parseApiResponse(InputStream inputStream) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(inputStream);

            // Récupérer les items (les questions)
            JsonNode items = root.get("items");
            if (items == null || !items.isArray()) {
                return List.of();
            }

            // Extraire les titres des questions
            List<String> questions = new ArrayList<>();
            for (JsonNode item : items) {
                String title = item.get("title").asText();
                questions.add(title);
            }
            return questions;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
}}
