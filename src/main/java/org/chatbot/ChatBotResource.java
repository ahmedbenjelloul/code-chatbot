package org.chatbot;



import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.MediaType;

@Path("/chatbot")
public class ChatBotResource {

    @Inject
    ChatBot chatBot;

    @POST
    @Path("/message")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public void chat(@Suspended AsyncResponse asyncResponse, String message) {
        // Process the message asynchronously
        asyncResponse.resume(chatBot.processMessage(message));
    }
}
