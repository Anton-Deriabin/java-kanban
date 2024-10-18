package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.NotFoundException;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import typeadapter.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gsonEpic = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Epic.class, new EpicSerializer())
            .registerTypeAdapter(Epic.class, new EpicDeserializer())
            .registerTypeAdapter(Duration.class, new DurationSerializer())
            .registerTypeAdapter(Duration.class, new DurationDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
            .create();
    private final Gson gsonSubtask = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
            .registerTypeAdapter(Subtask.class, new SubtaskDeserializer())
            .registerTypeAdapter(Duration.class, new DurationSerializer())
            .registerTypeAdapter(Duration.class, new DurationDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
            .create();

    public EpicHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String queryGet = exchange.getRequestURI().getQuery();
            switch (exchange.getRequestMethod()) {
                case "GET":
                    if (queryGet != null && queryGet.matches("id=\\d+")) {
                        int epicId = Integer.parseInt(queryGet.split("=")[1]);
                        if (path.endsWith("/subtasks")) {
                            Epic epic = manager.getEpic(epicId).get();
                            String response = gsonSubtask.toJson(manager.printSubtusksOfEpic(epic));
                            sendText(exchange, response);
                        } else {
                            Optional epicOptional = manager.getEpic(epicId);
                            Epic epic = (Epic) epicOptional.get();
                            String response = gsonEpic.toJson(epic);
                            sendText(exchange, response);
                        }
                    } else if (queryGet == null) {
                        String response = gsonEpic.toJson(manager.printEpics());
                        sendText(exchange, response);
                    }
                    break;
                case "POST":
                    String queryPost = exchange.getRequestURI().getQuery();
                    if (queryPost != null && queryPost.matches("id=\\d+")) {
                        String jsonEpic = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        int epicId = Integer.parseInt(queryPost.split("=")[1]);
                        Epic newEpic = gsonEpic.fromJson(jsonEpic, Epic.class);
                        newEpic.setId(epicId);
                        manager.updateEpic(newEpic);
                        sendHeaders(exchange);
                    } else if (queryPost == null) {
                        String jsonEpic = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Epic newEpic = gsonEpic.fromJson(jsonEpic, Epic.class);
                        manager.addEpic(newEpic);
                        sendHeaders(exchange);
                    }
                    break;
                case "DELETE":
                    String queryDelete = exchange.getRequestURI().getQuery();
                    if (queryDelete != null && queryDelete.matches("id=\\d+")) {
                        int epicId = Integer.parseInt(queryDelete.split("=")[1]);
                        manager.deleteEpic(epicId);
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
