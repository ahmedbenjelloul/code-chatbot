package org.chatbot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import opennlp.tools.util.Span;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Intent {
    private String[] tokens;
    private String[] tags;
    private Span[] names;

    // Check if the message contains a specific keyword (like 'help')
    public boolean containsKeyword(String keyword) {
        for (String token : tokens) {
            if (token.equalsIgnoreCase(keyword)) {
                return true;
            }
        }
        return false;
    }

    // Check if the message contains a named entity (like a person's name)
    public boolean containsEntity(String entity) {
        for (Span name : names) {
            String foundEntity = tokens[name.getStart()];
            if (foundEntity.equalsIgnoreCase(entity)) {
                return true;
            }
        }
        return false;
    }
}
