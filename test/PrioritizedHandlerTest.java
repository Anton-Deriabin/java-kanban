import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import typeadapter.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrioritizedHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Task.class, new TaskSerializer())
            .registerTypeAdapter(Task.class, new TaskDeserializer())
            .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
            .registerTypeAdapter(Subtask.class, new SubtaskDeserializer())
            .registerTypeAdapter(Duration.class, new DurationSerializer())
            .registerTypeAdapter(Duration.class, new DurationDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
            .create();

    public PrioritizedHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.clearTasks();
        manager.clearSubtasks();
        manager.clearEpics();
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
        manager.setNextId(1);
    }

    @Test
    public void prioritizedHandlerShouldReturnTasksInCorrectOrder() throws IOException, InterruptedException {
        Task task = new Task("Стрижка",
                "Подстричься",
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        manager.addTask(task);
        Epic epic = new Epic("Переезд", "Переехать в город Дубай");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Переезд",
                "Собрать вещи",
                epic.getId(),
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 8, 23, 10, 20));  // Подзадача раньше задачи
        manager.addSubtask(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Set<Task> prioritizedTasksSet = manager.getPrioritizedTasks();
        List<Task> prioritizedTasks = prioritizedTasksSet.stream().toList();
        String expectedJson = gson.toJson(prioritizedTasks);
        String responseBody = response.body();
        assertEquals(expectedJson, responseBody, "Список приоритетных задач некорректен");
        assertEquals(subtask.getId(), prioritizedTasks.get(0).getId(), "Подзадача должна быть первой в списке");
        assertEquals(task.getId(), prioritizedTasks.get(1).getId(), "Задача должна быть второй в списке");
    }
}

