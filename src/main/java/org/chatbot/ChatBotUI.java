package org.chatbot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChatBotUI {

    private static JTextArea chatArea;
    private static JTextField userInputField;
    private static JButton sendButton;

    public static void main(String[] args) {
        // Créer la fenêtre
        JFrame frame = new JFrame("ChatBot");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Zone de chat
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Champ de saisie utilisateur
        userInputField = new JTextField(30);
        sendButton = new JButton("Send");

        // Panel en bas
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(userInputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        // Ajouter les composants à la fenêtre
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        // Action du bouton Send
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userMessage = userInputField.getText();
                sendMessageToChatBot(userMessage);
            }
        });

        // Action de l'Entrée
        userInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userMessage = userInputField.getText();
                sendMessageToChatBot(userMessage);
            }
        });

        // Afficher la fenêtre
        frame.setVisible(true);
    }

    // Méthode pour envoyer un message au chatbot
    private static void sendMessageToChatBot(String message) {
        chatArea.append("You: " + message + "\n");
        userInputField.setText(""); // Effacer le champ de texte

        try {
            // Envoyer le message au backend Quarkus
            String botResponse = sendPostRequest(message);
            chatArea.append("Bot: " + botResponse + "\n");
        } catch (IOException e) {
            chatArea.append("Bot: Error connecting to server.\n");
        }
    }

    // Méthode pour envoyer une requête POST à Quarkus et recevoir la réponse
    private static String sendPostRequest(String message) throws IOException {
        URL url = new URL("http://localhost:8080/chatbot/message");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/plain; utf-8");
        conn.setDoOutput(true);

        // Envoyer le message à Quarkus
        byte[] input = message.getBytes(StandardCharsets.UTF_8);
        conn.getOutputStream().write(input, 0, input.length);

        // Lire la réponse de Quarkus
        Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8);
        String response = scanner.useDelimiter("\\A").next();
        scanner.close();

        return response;
    }
}
