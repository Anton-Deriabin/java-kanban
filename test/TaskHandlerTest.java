import com.google.gson.*;
import manager.NotFoundException;
import manager.InMemoryTaskManager;
import manager.TaskManager;
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
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TaskHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Task.class, new TaskSerializer())
            .registerTypeAdapter(Task.class, new TaskDeserializer())
            .registerTypeAdapter(Duration.class, new DurationSerializer())
            .registerTypeAdapter(Duration.class, new DurationDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
            .create();
    String messageReturn = "Задачи не возвращаются";
    String messageValue = "Некорректное количество";
    String messageName = "Некорректное имя";
    String messageDescription = "Некорректное описание";
    String messageDuration = "Некорректная длительность";
    String messageStartTime = "Некорректное время начала";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public TaskHandlerTest() throws IOException {
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
    public void taskHandlerShouldAddTask() throws IOException, InterruptedException {
        Task task = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.
                newBuilder().
                uri(url).
                POST(HttpRequest.BodyPublishers.ofString(taskJson)).
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertNotNull(manager.printTasks(), messageReturn);
        assertEquals(1, manager.printTasks().size(), messageValue);
        Optional task2Optional = manager.getTask(1);
        Task task3 = (Task) task2Optional.get();
        assertEquals("Переезд", task3.getName(), messageName);
        assertEquals("Собрать вещи", task3.getDescription(), messageDescription);
        assertEquals(Duration.ofMinutes(10), task3.getDuration(), messageDuration);
        assertEquals(LocalDateTime.of(2024, 9, 23, 10, 20), task3.getStartTime(),
                messageStartTime);
    }

    @Test
    public void taskHandlerShouldUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        manager.addTask(task);
        Task task2 = new Task("Переезд продолжение",
                "Собрать оставшиеся вещи",
                Duration.ofMinutes(120),
                LocalDateTime.of(2024, 9, 24, 10, 20));
        String taskJson = gson.toJson(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks?id=1&status=DONE");
        HttpRequest request = HttpRequest.
                newBuilder().
                uri(url).
                POST(HttpRequest.BodyPublishers.ofString(taskJson)).
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertNotNull(manager.printTasks(), messageReturn);
        assertEquals(1, manager.printTasks().size(), messageValue);
        Optional task2Optional = manager.getTask(1);
        Task task3 = (Task) task2Optional.get();
        assertEquals("Переезд продолжение", task3.getName(), messageName);
        assertEquals("Собрать оставшиеся вещи", task3.getDescription(), messageDescription);
        assertEquals(Duration.ofMinutes(120), task3.getDuration(), messageDuration);
        assertEquals(LocalDateTime.of(2024, 9, 24, 10, 20), task3.getStartTime(),
                messageStartTime);
        Task task4 = new Task("Стрижка",
                "Подстричься",
                Duration.ofMinutes(45),
                LocalDateTime.of(2024, 9, 25, 10, 20));
        manager.addTask(task4);
        Task task5 = new Task("Стрижка",
                "Подстричься и побрить бороду",
                Duration.ofMinutes(45),
                LocalDateTime.of(2024, 9, 26, 10, 20));
        String taskJson2 = gson.toJson(task5);
        URI url2 = URI.create("http://localhost:8080/tasks?id=2");
        HttpRequest request2 = HttpRequest.
                newBuilder().
                uri(url2).
                POST(HttpRequest.BodyPublishers.ofString(taskJson2)).
                build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());
        assertNotNull(manager.printTasks(), messageReturn);
        assertEquals(2, manager.printTasks().size(), messageValue);
        Optional task4Optional = manager.getTask(2);
        Task task6 = (Task) task4Optional.get();
        assertEquals("Стрижка", task6.getName(), messageName);
        assertEquals("Подстричься и побрить бороду", task6.getDescription(), messageDescription);
        assertEquals(Duration.ofMinutes(45), task6.getDuration(), messageDuration);
        assertEquals(LocalDateTime.of(2024, 9, 26, 10, 20), task6.getStartTime(),
                messageStartTime);
    }

    @Test
    public void taskHandlerShouldGetTask() throws IOException, InterruptedException {
        Task task = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        manager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks?id=1");
        HttpRequest request = HttpRequest.
                newBuilder().
                uri(url).
                GET().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        assertEquals(task.getName(), name, messageName);
        String description = jsonObject.get("description").getAsString();
        assertEquals(task.getDescription(), description, messageDescription);
        long minutes = jsonObject.get("duration").getAsLong();
        Duration duration = Duration.ofMinutes(minutes);
        assertEquals(task.getDuration(), duration, messageDuration);
        LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString(), formatter);
        assertEquals(task.getStartTime(), startTime, messageStartTime);
        Task task2 = new Task("Переезд продолжение",
                "Собрать оставшиеся вещи",
                Duration.ofMinutes(120),
                LocalDateTime.of(2024, 9, 24, 10, 20));
        manager.addTask(task2);
        URI url2 = URI.create("http://localhost:8080/tasks");
        HttpRequest request2 = HttpRequest.
                newBuilder().
                uri(url2).
                GET().
                build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());
        JsonElement jsonElement2 = JsonParser.parseString(response2.body());
        JsonObject jsonObject2 = jsonElement2.getAsJsonObject();
        JsonObject taskObject2 = jsonObject2.getAsJsonObject("2");
        String name2 = taskObject2.get("name").getAsString();
        assertEquals(task2.getName(), name2, messageName);
        String description2 = taskObject2.get("description").getAsString();
        assertEquals(task2.getDescription(), description2, messageDescription);
        long minutes2 = taskObject2.get("duration").getAsLong();
        Duration duration2 = Duration.ofMinutes(minutes2);
        assertEquals(task2.getDuration(), duration2, messageDuration);
        LocalDateTime startTime2 = LocalDateTime.parse(taskObject2.get("startTime").getAsString(), formatter);
        assertEquals(task2.getStartTime(), startTime2, messageStartTime);
        JsonObject taskObject1 = jsonObject2.getAsJsonObject("1");
        String name3 = taskObject1.get("name").getAsString();
        assertEquals(task.getName(), name3, messageName);
        String description3 = taskObject1.get("description").getAsString();
        assertEquals(task.getDescription(), description3, messageDescription);
        long minutes3 = taskObject1.get("duration").getAsLong();
        Duration duration3 = Duration.ofMinutes(minutes3);
        assertEquals(task.getDuration(), duration3, messageDuration);
        LocalDateTime startTime3 = LocalDateTime.parse(taskObject1.get("startTime").getAsString(), formatter);
        assertEquals(task.getStartTime(), startTime3, messageStartTime);
    }

    @Test
    public void taskHandlerShouldDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        manager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks?id=1");
        HttpRequest request = HttpRequest.
                newBuilder().
                uri(url).
                DELETE().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertThrows(NotFoundException.class, () -> {
            manager.getTask(task.getId());
        }, "Задача не удалена");
    }
}
