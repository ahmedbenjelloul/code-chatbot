package org.chatbot;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;

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

import java.io.InputStream;
import java.util.Arrays;

@Path("/chatbot")
public class GreetingResource {

    // Use SLF4J logger
    private static final Logger LOGGER = LoggerFactory.getLogger(GreetingResource.class);

    private final NameFinderME nameFinder;
    private final POSTaggerME posTagger;
    private final SentenceDetectorME sentenceDetector;
    private final TokenizerME tokenizer;

    public GreetingResource() {
        this.tokenizer = loadTokenizerModel();
        this.posTagger = loadPOSTaggerModel();
        this.nameFinder = loadNameFinderModel();
        this.sentenceDetector = loadSentenceDetectorModel();
    }

    @POST
    @Path("/message")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public void chat(@Suspended AsyncResponse asyncResponse, String message) {
        LOGGER.info("Received message: {}", message);

        if (tokenizer == null || posTagger == null || nameFinder == null || sentenceDetector == null) {
            LOGGER.error("Error: One or more OpenNLP models could not be loaded.");
            asyncResponse.resume("Error: One or more OpenNLP models could not be loaded.");
            return;
        }

        // Tokenization
        String[] tokens = tokenizer.tokenize(message);
        LOGGER.debug("Tokens: {}", Arrays.toString(tokens));

        // Part of Speech Tagging
        String[] tags = posTagger.tag(tokens);
        LOGGER.debug("Tags: {}", Arrays.toString(tags));

        // Named Entity Recognition
        Span[] names = nameFinder.find(tokens);
        LOGGER.debug("Named Entities: {}", Arrays.toString(names));

        // Intent analysis based on tokens and tags
        Intent intent = new Intent(tokens, tags, names);

        // Response generation based on detected intent
        if (containsGreeting(message)) {
            asyncResponse.resume("Hello! How can I assist you?");
        } else if (intent.containsKeyword("help")) {
            asyncResponse.resume("I'm here to help! What do you need?");
        } else if (intent.containsKeyword("goodbye")) {
            asyncResponse.resume("Goodbye!");
        } else {
            asyncResponse.resume("I'm sorry, I didn't understand. Can you rephrase?");
        }
    }

    // Method to detect greetings in the message
    private boolean containsGreeting(String message) {
        String lowercaseMessage = message.toLowerCase();
        String[] greetings = {"hello", "hi", "greetings", "hey"};
        return Arrays.stream(greetings).anyMatch(lowercaseMessage::contains);
    }

    // Helper methods to load OpenNLP models with proper exception handling
    private TokenizerME loadTokenizerModel() {
        return loadModel("/opennlp-en-ud-ewt-tokens.bin", TokenizerModel::new, TokenizerME::new);
    }

    private POSTaggerME loadPOSTaggerModel() {
        return loadModel("/opennlp-en-ud-ewt-pos.bin", POSModel::new, POSTaggerME::new);
    }

    private NameFinderME loadNameFinderModel() {
        return loadModel("/en-ner-person.bin", TokenNameFinderModel::new, NameFinderME::new);
    }

    private SentenceDetectorME loadSentenceDetectorModel() {
        return loadModel("/opennlp-en-ud-ewt-sentence.bin", SentenceModel::new, SentenceDetectorME::new);
    }

    // Generic method to load an OpenNLP model and handle exceptions
    private <T, M> T loadModel(String modelPath, ModelLoader<M> modelLoader, ComponentCreator<T, M> componentCreator) {
        try (InputStream modelStream = getClass().getResourceAsStream(modelPath)) {
            if (modelStream == null) {
                throw new IllegalArgumentException("Model file not found: " + modelPath);
            }
            M model = modelLoader.loadModel(modelStream);
            return componentCreator.createComponent(model);
        } catch (Exception e) {
            LOGGER.error("Failed to load model: {}", modelPath, e);
            return null;
        }
    }

    // Functional interfaces to abstract model loading and component creation
    @FunctionalInterface
    private interface ModelLoader<M> {
        M loadModel(InputStream inputStream) throws Exception;
    }

    @FunctionalInterface
    private interface ComponentCreator<T, M> {
        T createComponent(M model);
    }
}
