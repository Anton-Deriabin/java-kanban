package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.NotFoundException;
import manager.TaskManager;
import model.Task;
import status.Status;
import typeadapter.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Task.class, new TaskSerializer())
            .registerTypeAdapter(Task.class, new TaskDeserializer())
            .registerTypeAdapter(Duration.class, new DurationSerializer())
            .registerTypeAdapter(Duration.class, new DurationDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
            .create();

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    String queryGet = exchange.getRequestURI().getQuery();
                    if (queryGet != null && queryGet.startsWith("id=")) {
                        int taskId = Integer.parseInt(queryGet.split("=")[1]);
                        Optional taskOptional = manager.getTask(taskId);
                        Task task = (Task) taskOptional.get();
                        String response = gson.toJson(task);
                        sendText(exchange, response);
                    } else if (queryGet == null) {
                        String response = gson.toJson(manager.printTasks());
                        sendText(exchange, response);
                    }
                    break;
                case "POST":
                    String queryPost = exchange.getRequestURI().getQuery();
                    if (queryPost != null && queryPost.matches("id=\\d+")) {
                        String jsonTask = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        int taskId = Integer.parseInt(queryPost.split("=")[1]);
                        Task newTask = gson.fromJson(jsonTask, Task.class);
                        newTask.setId(taskId);
                        manager.updateTask(newTask);
                        sendHeaders(exchange);
                    } else if (queryPost != null && queryPost.matches("id=\\d+&status=[A-Z]+")) {
                        String[] params = queryPost.split("&");
                        int taskId = Integer.parseInt(params[0].split("=")[1]);
                        String status = params[1].split("=")[1];
                        String jsonTask = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Task newTask = gson.fromJson(jsonTask, Task.class);
                        newTask.setStatus(Status.valueOf(status));
                        newTask.setId(taskId);
                        manager.updateTask(newTask);
                        sendHeaders(exchange);
                    } else if (queryPost == null) {
                        String jsonTask = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Task newTask = gson.fromJson(jsonTask, Task.class);
                        manager.addTask(newTask);
                        sendHeaders(exchange);
                    }
                    break;
                case "DELETE":
                    String queryDelete = exchange.getRequestURI().getQuery();
                    if (queryDelete != null && queryDelete.startsWith("id=")) {
                        int taskId = Integer.parseInt(queryDelete.split("=")[1]);
                        manager.deleteTask(taskId);
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

