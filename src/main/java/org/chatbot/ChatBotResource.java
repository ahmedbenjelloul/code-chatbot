package org.chatbot;



import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/chatbot")
public class ChatBotResource {

    @Inject
    ChatBot chatBot;

    @POST
    @Path("/message")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
//    public void chat(@Suspended AsyncResponse asyncResponse, String message) {
//        // Process the message asynchronously
//        asyncResponse.resume(chatBot.processMessage(message));
//    }
    public String handleMessage(String message) {
        return chatBot.processMessage(message);
    }

    @Inject
    DataEnrichmentService dataEnrichmentService;

    @POST
    @Path("/enrichment")
    @Transactional
    public Response enrichDatabaseNow() {
        dataEnrichmentService.fetchAndEnrichData();
        return Response.ok("Enrichment process completed!").build();
    }


}
