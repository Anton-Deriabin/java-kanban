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
import typeadapter.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class EpicHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    private final Gson gsonEpic = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Epic.class, new EpicSerializer())
            .registerTypeAdapter(Epic.class, new EpicDeserializer())
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

    public EpicHandlerTest() throws IOException {
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
    public void epicHandlerShouldAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Переезд", "Переехать в город Дубай");
        String epicJson = gsonEpic.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.
                newBuilder().
                uri(url).
                POST(HttpRequest.BodyPublishers.ofString(epicJson)).
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertNotNull(manager.printEpics(), messageReturn);
        assertEquals(1, manager.printEpics().size(), messageValue);
        Optional epic2Optional = manager.getEpic(1);
        Epic epic2 = (Epic) epic2Optional.get();
        assertEquals("Переезд", epic2.getName(), messageName);
        assertEquals("Переехать в город Дубай", epic2.getDescription(), messageDescription);
        assertNull(epic2.getDuration(), messageDuration);
        assertNull(epic2.getStartTime(),
                messageStartTime);
    }

    @Test
    public void epicHandlerShouldUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Переезд", "Переехать в город Дубай");
        manager.addEpic(epic);
        Subtask task = new Subtask("Переезд",
                "Собрать вещи",
                1,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        manager.addSubtask(task);
        Epic epic2 = new Epic("Переезд2", "Переехать в город Щелково");
        String epicJson2 = gsonEpic.toJson(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics?id=1");
        HttpRequest request = HttpRequest.
                newBuilder().
                uri(url).
                POST(HttpRequest.BodyPublishers.ofString(epicJson2)).
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertNotNull(manager.printEpics(), messageReturn);
        assertNotNull(manager.printSubtasks(), messageReturn);
        assertEquals(1, manager.printEpics().size(), messageValue);
        Optional epic3Optional = manager.getEpic(1);
        Epic epic3 = (Epic) epic3Optional.get();
        assertEquals(1, epic3.getEpicSubtusks().size(), messageValue);
        assertEquals("Переезд", epic3.getEpicSubtusks().get(2).getName(), messageName);
        assertEquals("Собрать вещи", epic3.getEpicSubtusks().get(2).getDescription(), messageDescription);
        assertEquals(Duration.ofMinutes(10), epic3.getEpicSubtusks().get(2).getDuration(), messageDuration);
        assertEquals(LocalDateTime.of(2024, 9, 23, 10, 20),
                epic3.getEpicSubtusks().get(2).getStartTime(), messageStartTime);
        assertEquals("Переезд2", epic3.getName(), messageName);
        assertEquals("Переехать в город Щелково", epic3.getDescription(), messageDescription);
    }

    @Test
    public void epicHandlerShouldGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Переезд", "Переехать в город Дубай");
        manager.addEpic(epic);
        Subtask task = new Subtask("Переезд",
                "Собрать вещи",
                1,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        manager.addSubtask(task);
        Epic epic2 = new Epic("Переезд2", "Переехать в город Щелково");
        manager.addEpic(epic2);
        Subtask task2 = new Subtask("Переезд",
                "Собрать вещи",
                3,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 10, 23, 10, 20));
        manager.addSubtask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics?id=1");
        HttpRequest request = HttpRequest.
                newBuilder().
                uri(url).
                GET().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNotNull(manager.printEpics(), messageReturn);
        assertNotNull(manager.printSubtasks(), messageReturn);
        assertEquals(2, manager.printEpics().size(), messageValue);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        assertEquals(epic.getName(), name, messageName);
        String description = jsonObject.get("description").getAsString();
        assertEquals(epic.getDescription(), description, messageDescription);
        long minutes = jsonObject.get("duration").getAsLong();
        Duration duration = Duration.ofMinutes(minutes);
        assertEquals(epic.getDuration(), duration, messageDuration);
        LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString(), formatter);
        assertEquals(epic.getStartTime(), startTime, messageStartTime);
        JsonObject subtaskJson = jsonObject.getAsJsonObject("epicSubtasks");
        assertNotNull(subtaskJson, messageValue);
        for (Map.Entry<Integer, Subtask> entry : epic.getEpicSubtusks().entrySet()) {
            int subtaskId = entry.getKey();
            Subtask subtask = entry.getValue();
            JsonObject jsonSubtask = subtaskJson.getAsJsonObject(String.valueOf(subtaskId));
            String subtaskName = jsonSubtask.get("name").getAsString();
            assertEquals(subtask.getName(), subtaskName, messageName);
            String subtaskDescription = jsonSubtask.get("description").getAsString();
            assertEquals(subtask.getDescription(), subtaskDescription, messageDescription);
            long subtaskMinutes = jsonSubtask.get("duration").getAsLong();
            Duration subtaskDuration = Duration.ofMinutes(subtaskMinutes);
            assertEquals(subtask.getDuration(), subtaskDuration, messageDuration);
            LocalDateTime subtaskStartTime = LocalDateTime.parse(jsonSubtask.get("startTime").getAsString(), formatter);
            assertEquals(subtask.getStartTime(), subtaskStartTime, messageStartTime);
        }

        URI url2 = URI.create("http://localhost:8080/epics");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());
        JsonElement jsonElement2 = JsonParser.parseString(response2.body());
        JsonObject jsonObject2 = jsonElement2.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
            JsonObject jsonEpic = entry.getValue().getAsJsonObject();
            String epicName = jsonEpic.get("name").getAsString();
            Epic expectedEpic = (epicName.equals(epic.getName())) ? epic : epic2;
            assertEquals(expectedEpic.getName(), epicName, messageName);
            String epicDescription = jsonEpic.get("description").getAsString();
            assertEquals(expectedEpic.getDescription(), epicDescription, messageDescription);
            long epicMinutes = jsonEpic.get("duration").getAsLong();
            Duration epicDuration = Duration.ofMinutes(epicMinutes);
            assertEquals(expectedEpic.getDuration(), epicDuration, messageDuration);
            LocalDateTime epicStartTime = LocalDateTime.parse(jsonEpic.get("startTime").getAsString(), formatter);
            assertEquals(expectedEpic.getStartTime(), epicStartTime, messageStartTime);
            JsonObject subtaskJson2 = jsonEpic.getAsJsonObject("epicSubtasks");
            assertNotNull(subtaskJson2, messageValue);
            for (Map.Entry<Integer, Subtask> subtaskEntry : expectedEpic.getEpicSubtusks().entrySet()) {
                int subtaskId = subtaskEntry.getKey();
                Subtask expectedSubtask = subtaskEntry.getValue();
                JsonObject jsonSubtask2 = subtaskJson2.getAsJsonObject(String.valueOf(subtaskId));
                String subtaskName = jsonSubtask2.get("name").getAsString();
                assertEquals(expectedSubtask.getName(), subtaskName, messageName);
                String subtaskDescription = jsonSubtask2.get("description").getAsString();
                assertEquals(expectedSubtask.getDescription(), subtaskDescription, messageDescription);
                long subtaskMinutes = jsonSubtask2.get("duration").getAsLong();
                Duration subtaskDuration = Duration.ofMinutes(subtaskMinutes);
                assertEquals(expectedSubtask.getDuration(), subtaskDuration, messageDuration);
                LocalDateTime subtaskStartTime = LocalDateTime.parse(jsonSubtask2.get("startTime").getAsString(), formatter);
                assertEquals(expectedSubtask.getStartTime(), subtaskStartTime, messageStartTime);
            }
        }
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
        URI url = URI.create("http://localhost:8080/epics?id=1");
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
        assertThrows(NotFoundException.class, () -> {
            manager.getEpic(epic.getId());
        }, "Эпик не удален из списка эпиков");
    }
}
