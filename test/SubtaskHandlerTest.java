import com.google.gson.*;
import manager.NotFoundException;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import status.Status;
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

public class SubtaskHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
            .registerTypeAdapter(Subtask.class, new SubtaskDeserializer())
            .registerTypeAdapter(Duration.class, new DurationSerializer())
            .registerTypeAdapter(Duration.class, new DurationDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
            .create();
    String messageReturn = "Задачи не возвращаются";
    String messageValue = "Некорректное количество";
    String messageName = "Некорректное имя";
    String messageDescription = "Некорректное описание";
    String messageEpicId = "Некорректный ID эпика у подзадачи";
    String messageDuration = "Некорректная длительность";
    String messageStartTime = "Некорректное время начала";
    String messageStatus = "Некорректный статус";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public SubtaskHandlerTest() throws IOException {
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
    public void taskHandlerShouldAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Переезд", "Переехать в город Дубай");
        manager.addEpic(epic);
        Subtask task = new Subtask("Переезд",
                "Собрать вещи",
                1,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.
                newBuilder().
                uri(url).
                POST(HttpRequest.BodyPublishers.ofString(taskJson)).
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertNotNull(manager.printSubtasks(), messageReturn);
        assertNotNull(epic.getEpicSubtusks(), messageReturn);
        assertEquals(1, manager.printSubtasks().size(), messageValue);
        assertEquals(1, epic.getEpicSubtusks().size(), messageValue);
        Optional task2Optional = manager.getSubtask(2);
        Subtask task3 = (Subtask) task2Optional.get();
        assertEquals("Переезд", task3.getName(), messageName);
        assertEquals("Собрать вещи", task3.getDescription(), messageDescription);
        assertEquals(Duration.ofMinutes(10), task3.getDuration(), messageDuration);
        assertEquals(LocalDateTime.of(2024, 9, 23, 10, 20),
                task3.getStartTime(),
                messageStartTime);
        Subtask task4 = epic.getEpicSubtusks().get(2);
        assertEquals("Переезд", task4.getName(), messageName);
        assertEquals("Собрать вещи", task4.getDescription(), messageDescription);
        assertEquals(Duration.ofMinutes(10), task4.getDuration(), messageDuration);
        assertEquals(LocalDateTime.of(2024, 9, 23, 10, 20),
                task3.getStartTime(),
                messageStartTime);
    }

    @Test
    public void taskHandlerShouldUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Переезд", "Переехать в город Дубай");
        manager.addEpic(epic);
        Subtask task = new Subtask("Переезд",
                "Собрать вещи",
                1,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        manager.addSubtask(task);
        Subtask task2 = new Subtask("Переезд",
                "Купить билеты",
                1,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 10, 23, 10, 20));
        manager.addSubtask(task2);
        Subtask task3 = new Subtask("Переезд продолжение",
                "Собрать оставшиеся вещи",
                1,
                Duration.ofMinutes(120),
                LocalDateTime.of(2024, 9, 24, 10, 20));
        String taskJson = gson.toJson(task3);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks?id=2&status=INPROGRESS");
        HttpRequest request = HttpRequest.
                newBuilder().
                uri(url).
                POST(HttpRequest.BodyPublishers.ofString(taskJson)).
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("Переезд продолжение", epic.getEpicSubtusks().get(2).getName(),
                messageName);
        assertEquals("Собрать оставшиеся вещи", epic.getEpicSubtusks().get(2).getDescription(),
                messageDescription);
        assertEquals(Duration.ofMinutes(120), epic.getEpicSubtusks().get(2).getDuration(),
                messageDuration);
        assertEquals(LocalDateTime.of(2024, 9, 24, 10, 20),
                epic.getEpicSubtusks().get(2).getStartTime(),
                messageStartTime);
        assertEquals(Status.INPROGRESS, epic.getStatus(), messageStatus);
        assertEquals(Status.INPROGRESS, epic.getEpicSubtusks().get(2).getStatus(),
                messageStatus);
        assertEquals(Status.NEW, epic.getEpicSubtusks().get(3).getStatus(),
                messageStatus);
        Optional task4Optional = manager.getSubtask(2);
        Subtask task4 = (Subtask) task4Optional.get();
        Optional task5Optional = manager.getSubtask(3);
        Subtask task5 = (Subtask) task5Optional.get();
        assertEquals(Status.INPROGRESS, task4.getStatus(),
                messageEpicId);
        assertEquals(Status.NEW, task5.getStatus(),
                messageEpicId);
        assertEquals("Переезд продолжение", task4.getName(),
                messageName);
        assertEquals("Собрать оставшиеся вещи", task4.getDescription(),
                messageDescription);
        assertEquals(Duration.ofMinutes(120), task4.getDuration(),
                messageDuration);
        assertEquals(LocalDateTime.of(2024, 9, 24, 10, 20),
                task3.getStartTime(),
                messageStartTime);
        manager.clearSubtasks();
        manager.clearEpics();

        Epic epic2 = new Epic("Переезд", "Переехать в город Дубай");
        manager.addEpic(epic2);
        Subtask task6 = new Subtask("Переезд",
                "Собрать вещи",
                4,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        manager.addSubtask(task6);
        Subtask task7 = new Subtask("Переезд продолжение",
                "Собрать оставшиеся вещи",
                4,
                Duration.ofMinutes(120),
                LocalDateTime.of(2024, 9, 24, 10, 20));
        String taskJson2 = gson.toJson(task7);
        URI url2 = URI.create("http://localhost:8080/subtasks?id=5");
        HttpRequest request2 = HttpRequest.
                newBuilder().
                uri(url2).
                POST(HttpRequest.BodyPublishers.ofString(taskJson2)).
                build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());
        assertEquals("Переезд продолжение", epic2.getEpicSubtusks().get(5).getName(),
                messageName);
        assertEquals("Собрать оставшиеся вещи", epic2.getEpicSubtusks().get(5).getDescription(),
                messageDescription);
        assertEquals(Duration.ofMinutes(120), epic2.getEpicSubtusks().get(5).getDuration(),
                messageDuration);
        assertEquals(LocalDateTime.of(2024, 9, 24, 10, 20),
                epic2.getEpicSubtusks().get(5).getStartTime(),
                messageStartTime);
        assertEquals(Status.NEW, epic2.getStatus(), messageStatus);
        assertEquals(Status.NEW, epic2.getEpicSubtusks().get(5).getStatus(),
                messageStatus);
        Optional task8Optional = manager.getSubtask(5);
        Subtask task8 = (Subtask) task8Optional.get();
        assertEquals(Status.NEW, task8.getStatus(),
                messageEpicId);
        assertEquals(Status.NEW, task5.getStatus(),
                messageEpicId);
        assertEquals("Переезд продолжение", task8.getName(),
                messageName);
        assertEquals("Собрать оставшиеся вещи", task8.getDescription(),
                messageDescription);
        assertEquals(Duration.ofMinutes(120), task8.getDuration(),
                messageDuration);
        assertEquals(LocalDateTime.of(2024, 9, 24, 10, 20),
                task8.getStartTime(),
                messageStartTime);
    }

    @Test
    public void taskHandlerShouldGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Переезд", "Переехать в город Дубай");
        manager.addEpic(epic);
        Subtask task = new Subtask("Переезд",
                "Собрать вещи",
                1,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        manager.addSubtask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks?id=2");
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
        int subtasksEpicId = jsonObject.get("subtasksEpicId").getAsInt();
        assertEquals(task.getSubtasksEpicId(), subtasksEpicId, messageEpicId);
        long minutes = jsonObject.get("duration").getAsLong();
        Duration duration = Duration.ofMinutes(minutes);
        assertEquals(task.getDuration(), duration, messageDuration);
        LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString(), formatter);
        assertEquals(task.getStartTime(), startTime, messageStartTime);

        Subtask task2 = new Subtask("Переезд продолжение",
                "Собрать оставшиеся вещи",
                1,
                Duration.ofMinutes(120),
                LocalDateTime.of(2024, 9, 24, 10, 20));
        manager.addSubtask(task2);
        URI url2 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2 = HttpRequest.
                newBuilder().
                uri(url2).
                GET().
                build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());
        JsonElement jsonElement2 = JsonParser.parseString(response2.body());
        JsonObject jsonObject2 = jsonElement2.getAsJsonObject();
        JsonObject taskObject2 = jsonObject2.getAsJsonObject("3");
        String name2 = taskObject2.get("name").getAsString();
        assertEquals(task2.getName(), name2, messageName);
        String description2 = taskObject2.get("description").getAsString();
        assertEquals(task2.getDescription(), description2, messageDescription);
        int subtasksEpicId2 = jsonObject.get("subtasksEpicId").getAsInt();
        assertEquals(task2.getSubtasksEpicId(), subtasksEpicId2, messageEpicId);
        long minutes2 = taskObject2.get("duration").getAsLong();
        Duration duration2 = Duration.ofMinutes(minutes2);
        assertEquals(task2.getDuration(), duration2, messageDuration);
        LocalDateTime startTime2 = LocalDateTime.parse(taskObject2.get("startTime").getAsString(), formatter);
        assertEquals(task2.getStartTime(), startTime2, messageStartTime);
        JsonObject taskObject1 = jsonObject2.getAsJsonObject("2");
        String name3 = taskObject1.get("name").getAsString();
        assertEquals(task.getName(), name3, messageName);
        String description3 = taskObject1.get("description").getAsString();
        assertEquals(task.getDescription(), description3, messageDescription);
        int subtasksEpicId3 = jsonObject.get("subtasksEpicId").getAsInt();
        assertEquals(task.getSubtasksEpicId(), subtasksEpicId3, messageEpicId);
        long minutes3 = taskObject1.get("duration").getAsLong();
        Duration duration3 = Duration.ofMinutes(minutes3);
        assertEquals(task.getDuration(), duration3, messageDuration);
        LocalDateTime startTime3 = LocalDateTime.parse(taskObject1.get("startTime").getAsString(), formatter);
        assertEquals(task.getStartTime(), startTime3, messageStartTime);
    }

    @Test
    public void taskHandlerShouldDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Переезд", "Переехать в город Дубай");
        manager.addEpic(epic);
        Subtask task = new Subtask("Переезд",
                "Собрать вещи",
                1,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        manager.addSubtask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks?id=2");
        HttpRequest request = HttpRequest.
                newBuilder().
                uri(url).
                DELETE().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertThrows(NotFoundException.class, () -> {
            manager.getSubtask(task.getId());
        }, "Подзадача не удалена из списка подзадач");
        assertNull(epic.getEpicSubtusks().get(2), "Подзадача не удалена из списка эпика");

    }
}