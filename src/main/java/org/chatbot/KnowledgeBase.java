package org.chatbot;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "knowledge_base")
public class KnowledgeBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    @Column(name = "questionpattern")
    private String questionPattern;

    private String answer;

    // Constructors, getters, and setters
    public KnowledgeBase() {
    }

    public KnowledgeBase(String category, String questionPattern, String answer) {
        this.category = category;
        this.questionPattern = questionPattern;
        this.answer = answer;
    }
}
