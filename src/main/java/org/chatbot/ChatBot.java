package org.chatbot;

import jakarta.inject.Inject;

import jakarta.persistence.EntityManager;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.InputStream;
import java.util.Arrays;


@ApplicationScoped
public class ChatBot {

//    private static final Logger LOGGER = LoggerFactory.getLogger(ChatBot.class);
//
//    private final TokenizerME tokenizer;
//    private final POSTaggerME posTagger;
//    private final NameFinderME nameFinder;
//    private final SentenceDetectorME sentenceDetector;
//
//    public ChatBot() {
//        this.tokenizer = loadTokenizerModel();
//        this.posTagger = loadPOSTaggerModel();
//        this.nameFinder = loadNameFinderModel();
//        this.sentenceDetector = loadSentenceDetectorModel();
//    }
//
//    public String processMessage(String message) {
//        if (tokenizer == null || posTagger == null || nameFinder == null || sentenceDetector == null) {
//            LOGGER.error("Error: One or more OpenNLP models could not be loaded.");
//            return "Error: One or more OpenNLP models could not be loaded.";
//        }
//
//        // Tokenization
//        String[] tokens = tokenizer.tokenize(message);
//        LOGGER.debug("Tokens: {}", String.join(", ", tokens));
//
//        // Part of Speech Tagging
//        String[] tags = posTagger.tag(tokens);
//        LOGGER.debug("POS Tags: {}", String.join(", ", tags));
//
//        // Named Entity Recognition
//        Span[] names = nameFinder.find(tokens);
//        for (Span name : names) {
//            LOGGER.debug("Detected Named Entity: {}", name.toString());
//        }
//
//        // Logic for simple response generation
//        if (containsGreeting(message)) {
//            return "Hello! How can I assist you?";
//        } else if (containsKeyword(tokens, "help")) {
//            return "I'm here to help! What do you need?";
//        } else if (containsKeyword(tokens, "goodbye")) {
//            return "Goodbye!";
//        } else {
//            return "I'm sorry, I didn't understand. Can you rephrase?";
//        }
//    }
//
//    private boolean containsGreeting(String message) {
//        String[] greetings = {"hello", "hi", "greetings", "hey"};
//        return Arrays.stream(greetings).anyMatch(message.toLowerCase()::contains);
//    }
//
//    private boolean containsKeyword(String[] tokens, String keyword) {
//        return Arrays.stream(tokens).anyMatch(token -> token.equalsIgnoreCase(keyword));
//    }
//
//    // Methods to load models (as before)
//    private TokenizerME loadTokenizerModel() {
//        return loadModel("/opennlp-en-ud-ewt-tokens.bin", TokenizerModel::new, TokenizerME::new);
//    }
//
//    private POSTaggerME loadPOSTaggerModel() {
//        return loadModel("/opennlp-en-ud-ewt-pos.bin", POSModel::new, POSTaggerME::new);
//    }
//
//    private NameFinderME loadNameFinderModel() {
//        return loadModel("/en-ner-person.bin", TokenNameFinderModel::new, NameFinderME::new);
//    }
//
//    private SentenceDetectorME loadSentenceDetectorModel() {
//        return loadModel("/opennlp-en-ud-ewt-sentence.bin", SentenceModel::new, SentenceDetectorME::new);
//    }
//
//    private <T, M> T loadModel(String modelPath, ModelLoader<M> modelLoader, ComponentCreator<T, M> componentCreator) {
//        try (InputStream modelStream = getClass().getResourceAsStream(modelPath)) {
//            if (modelStream == null) {
//                throw new IllegalArgumentException("Model file not found: " + modelPath);
//            }
//            M model = modelLoader.loadModel(modelStream);
//            return componentCreator.createComponent(model);
//        } catch (Exception e) {
//            LOGGER.error("Failed to load model: {}", modelPath, e);
//            return null;
//        }
//    }
//
//    @FunctionalInterface
//    private interface ModelLoader<M> {
//        M loadModel(InputStream inputStream) throws Exception;
//    }
//
//    @FunctionalInterface
//    private interface ComponentCreator<T, M> {
//        T createComponent(M model);
//    }
@Inject
EntityManager entityManager;

    public String processMessage(String message) {
        String response = searchKnowledgeBase(message);
        if (response != null) {
            return response;
        }
        return "Je n'ai pas trouvé de réponse. Pouvez-vous préciser ?";
    }

    private String searchKnowledgeBase(String message) {
        String query = "SELECT k.answer FROM KnowledgeBase k WHERE :message ~* k.questionpattern";
        var results = entityManager.createNativeQuery(query)
                .setParameter("message", message)
                .getResultList();
        System.out.println("Résultats de la recherche : " + results);
        return results.isEmpty() ? null : (String) results.get(0);
    }


}