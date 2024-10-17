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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder()
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

    public HistoryHandlerTest() throws IOException {
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
    public void historyHandlerShouldReturnCorrectHistory() throws IOException, InterruptedException {
        Task task = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        manager.addTask(task);
        Epic epic = new Epic("Переезд", "Переехать в город Дубай");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Переезд",
                "Собрать вещи",
                2,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        manager.addSubtask(subtask);
        manager.getTask(task.getId());
        manager.getEpic(epic.getId());
        manager.getSubtask(subtask.getId());
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String responseBody = response.body();
        var history = manager.getHistory();
        assertEquals(3, history.size(), "Размер истории некорректен");
        String expectedJson = gson.toJson(history);
        assertEquals(expectedJson, responseBody, "История некорректна");
    }
}

