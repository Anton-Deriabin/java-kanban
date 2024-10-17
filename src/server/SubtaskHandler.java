package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.NotFoundException;
import manager.TaskManager;
import model.Subtask;
import status.Status;
import typeadapter.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
            .registerTypeAdapter(Subtask.class, new SubtaskDeserializer())
            .registerTypeAdapter(Duration.class, new DurationSerializer())
            .registerTypeAdapter(Duration.class, new DurationDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
            .create();

    public SubtaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    String queryGet = exchange.getRequestURI().getQuery();
                    if (queryGet != null && queryGet.startsWith("id=")) {
                        int subtaskId = Integer.parseInt(queryGet.split("=")[1]);
                        Optional subtaskOptional = manager.getSubtask(subtaskId);
                        Subtask subtask = (Subtask) subtaskOptional.get();
                        String response = gson.toJson(subtask);
                        sendText(exchange, response);
                    } else if (queryGet == null) {
                        String response = gson.toJson(manager.printSubtasks());
                        sendText(exchange, response);
                    }
                    break;
                case "POST":
                    String queryPost = exchange.getRequestURI().getQuery();
                    if (queryPost != null && queryPost.matches("id=\\d+")) {
                        String jsonSubtask = new String(exchange.getRequestBody().readAllBytes(),
                                StandardCharsets.UTF_8);
                        int subtaskId = Integer.parseInt(queryPost.split("=")[1]);
                        Subtask newSubtask = gson.fromJson(jsonSubtask, Subtask.class);
                        newSubtask.setId(subtaskId);
                        manager.updateSubtask(newSubtask);
                        sendHeaders(exchange);
                    } else if (queryPost != null && queryPost.matches("id=\\d+&status=[A-Z]+")) {
                        String[] params = queryPost.split("&");
                        int subtaskId = Integer.parseInt(params[0].split("=")[1]);
                        String status = params[1].split("=")[1];
                        String jsonSubtask = new String(exchange.getRequestBody().readAllBytes(),
                                StandardCharsets.UTF_8);
                        Subtask newSubtask = gson.fromJson(jsonSubtask, Subtask.class);
                        newSubtask.setStatus(Status.valueOf(status));
                        newSubtask.setId(subtaskId);
                        manager.updateSubtask(newSubtask);
                        sendHeaders(exchange);
                    } else if (queryPost == null) {
                        String jsonSubtask = new String(exchange.getRequestBody().readAllBytes(),
                                StandardCharsets.UTF_8);
                        Subtask newSubtask = gson.fromJson(jsonSubtask, Subtask.class);
                        manager.addSubtask(newSubtask);
                        sendHeaders(exchange);
                    }
                    break;
                case "DELETE":
                    String queryDelete = exchange.getRequestURI().getQuery();
                    if (queryDelete != null && queryDelete.startsWith("id=")) {
                        int subtaskId = Integer.parseInt(queryDelete.split("=")[1]);
                        manager.deleteSubtask(subtaskId);
                        sendHeaders(exchange);
                    }
                    break;
                default:
                    exchange.sendResponseHeaders(405, 0);
                    exchange.close();
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }
}