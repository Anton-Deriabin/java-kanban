package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.NotFoundException;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import typeadapter.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Task.class, new TaskSerializer())
            .registerTypeAdapter(Task.class, new TaskDeserializer())
            .registerTypeAdapter(Epic.class, new EpicSerializer())
            .registerTypeAdapter(Epic.class, new EpicDeserializer())
            .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
            .registerTypeAdapter(Subtask.class, new SubtaskDeserializer())
            .registerTypeAdapter(Duration.class, new DurationSerializer())
            .registerTypeAdapter(Duration.class, new DurationDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
            .create();

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                var history = taskManager.getHistory();
                String response = gson.toJson(history);
                sendText(exchange, response);
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }
}
