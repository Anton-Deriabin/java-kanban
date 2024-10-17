import manager.FileBackedTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import status.Status;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;
    protected Task task3;
    protected Epic epic3;
    protected Subtask subtask3;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    @BeforeEach
    @Override
    public void setUp() throws IOException {
        file = File.createTempFile("test", ".csv");
        taskManager = new FileBackedTaskManager(file);
        task1 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        task2 = new Task("Стрижка",
                "Сходить в барбершоп",
                Duration.ofHours(3),
                LocalDateTime.of(2024, 9, 24, 17, 0));
        epic1 = new Epic("Переезд", "Собрать вещи");
        epic2 = new Epic("Чертежи моста", "Сделать проект моста через реку Волга");
        subtask1 = new Subtask("Пролетное строение",
                "Начертить пролетное строение",
                1,
                Duration.ofDays(14),
                LocalDateTime.of(2024, 10, 13, 8, 0));
        subtask2 = new Subtask("Опоры",
                "Начертить опоры",
                1,
                Duration.ofDays(8),
                LocalDateTime.of(2024, 10, 28, 8, 0));
    }

    @AfterEach
    @Override
    public void setNextId() {
        super.setNextId();
    }

    @Test
    public void shouldLoadTasksFromFile() throws IOException {
        String fileContent = "Список сохраненных задач:\n" +
                "1,TASK,Переезд,NEW,Собрать вещи,60,10:20 23.09.2024\n" +
                "2,EPIC,Чертежи моста,NEW,Сделать проект моста через реку Волга,20160,08:00 13.10.2024\n" +
                "3,SUBTASK,Пролетное строение,NEW,Начертить пролетное строение,2,20160,08:00 13.10.2024\n";
        Files.writeString(file.toPath(), fileContent);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        HashMap<Integer, Task> tasks = loadedManager.printTasks();
        HashMap<Integer, Epic> epics = loadedManager.printEpics();
        HashMap<Integer, Subtask> subtasks = loadedManager.printSubtasks();
        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals(1, epics.size());
        Assertions.assertEquals(1, subtasks.size());
        Task task = tasks.get(1);
        Epic epic = epics.get(2);
        Subtask subtask = subtasks.get(3);
        Assertions.assertEquals("Переезд", task.getName());
        Assertions.assertEquals(Status.NEW, task.getStatus());
        Assertions.assertEquals(Duration.ofMinutes(60), task.getDuration());
        Assertions.assertEquals(LocalDateTime.of(2024, 9, 23, 10, 20), task.getStartTime());
        Assertions.assertEquals("Чертежи моста", epic.getName());
        Assertions.assertEquals(Status.NEW, epic.getStatus());
        Assertions.assertEquals(Duration.ofMinutes(20160), epic.getDuration());
        Assertions.assertEquals(LocalDateTime.of(2024, 10, 13, 8, 0), epic.getStartTime());
        Assertions.assertEquals("Пролетное строение", subtask.getName());
        Assertions.assertEquals(Status.NEW, subtask.getStatus());
        Assertions.assertEquals(2, subtask.getSubtasksEpicId());
        Assertions.assertEquals(Duration.ofMinutes(20160), subtask.getDuration());
        Assertions.assertEquals(LocalDateTime.of(2024, 10, 13, 8, 0), subtask.getStartTime());
    }

    @Test
    @Override
    public void taskManagerShouldAddTask() throws IOException {
        taskManager.addTask(task1);
        String savedData = Files.readString(file.toPath());
        String expectedData = "Список сохраненных задач:\n" +
                String.format("%d,TASK,Переезд,NEW,Собрать вещи,60,%s\n",
                        task1.getId(),
                        task1.getStartTime().format(formatter));
        Assertions.assertEquals(expectedData, savedData);
    }

    @Test
    @Override
    public void taskManagerShouldDeleteTask() throws IOException {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.deleteTask(task2.getId());
        String savedData = Files.readString(file.toPath());
        String expectedData = "Список сохраненных задач:\n" +
                String.format("%d,TASK,Переезд,NEW,Собрать вещи,60,%s\n",
                        task1.getId(),
                        task1.getStartTime().format(formatter));
        Assertions.assertEquals(expectedData, savedData);
    }

    @Test
    @Override
    public void taskManagerShouldClearTasks() throws IOException {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.clearTasks();
        String savedData = Files.readString(file.toPath());
        String expectedData = "Список сохраненных задач:\n";
        Assertions.assertEquals(expectedData, savedData);
    }

    @Test
    @Override
    public void taskManagerShouldUpdateTask() throws IOException {
        taskManager.addTask(task1);
        task3 = new Task("Переезд",
                "Собрать вещи",
                task1.getId(),
                Status.INPROGRESS,
                task1.getDuration(),
                task1.getStartTime());
        taskManager.updateTask(task3);
        String savedData = Files.readString(file.toPath());
        String expectedData = "Список сохраненных задач:\n" +
                String.format("%d,TASK,Переезд,INPROGRESS,Собрать вещи,60,%s\n",
                        task1.getId(),
                        task1.getStartTime().format(formatter));
        Assertions.assertEquals(expectedData, savedData);
    }

    @Test
    @Override
    public void taskManagerShouldAddEpic() throws IOException {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        String savedData = Files.readString(file.toPath());
        String expectedData = "Список сохраненных задач:\n" +
                String.format("%d,EPIC,Чертежи моста,NEW,Сделать проект моста через реку Волга,20160,%s\n",
                        epic2.getId(), subtask1.getStartTime().format(formatter)) +
                String.format("%d,SUBTASK,Пролетное строение,NEW,Начертить пролетное строение,%d,20160,%s\n",
                        subtask1.getId(), epic2.getId(), subtask1.getStartTime().format(formatter));
        Assertions.assertEquals(expectedData, savedData);
    }

    @Test
    @Override
    public void taskManagerShouldDeleteEpic() throws IOException {
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.deleteEpic(epic1.getId());
        String savedData = Files.readString(file.toPath());
        String expectedData = "Список сохраненных задач:\n" +
                String.format("%d,EPIC,Чертежи моста,NEW,Сделать проект моста через реку Волга,20160,%s\n",
                        epic2.getId(), subtask1.getStartTime().format(formatter)) +
                String.format("%d,SUBTASK,Пролетное строение,NEW,Начертить пролетное строение,%d,20160,%s\n",
                        subtask1.getId(), epic2.getId(), subtask1.getStartTime().format(formatter));
        Assertions.assertEquals(expectedData, savedData);
    }

    @Test
    @Override
    public void taskManagerShouldClearEpics() throws IOException {
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.clearEpics();
        String savedData = Files.readString(file.toPath());
        String expectedData = "Список сохраненных задач:\n";
        Assertions.assertEquals(expectedData, savedData);
    }

    @Test
    @Override
    public void taskManagerShouldUpdateEpic() throws IOException {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        epic3 = new Epic("Чертежи моста",
                "Сделать проект моста через реку Кама",
                epic2.getId(),
                epic2.getStatus(),
                epic2.getDuration(),
                epic2.getStartTime());
        taskManager.updateEpic(epic3);
        String savedData = Files.readString(file.toPath());
        String expectedData = "Список сохраненных задач:\n" +
                String.format("%d,EPIC,Чертежи моста,NEW,Сделать проект моста через реку Кама,20160,%s\n",
                        epic3.getId(), subtask1.getStartTime().format(formatter)) +
                String.format("%d,SUBTASK,Пролетное строение,NEW,Начертить пролетное строение,%d,20160,%s\n",
                        subtask1.getId(), epic2.getId(), subtask1.getStartTime().format(formatter));
        Assertions.assertEquals(expectedData, savedData);
    }

    @Test
    @Override
    public void taskManagerShouldAddSubtusk() throws IOException {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        String savedData = Files.readString(file.toPath());
        String expectedData = "Список сохраненных задач:\n" +
                String.format("%d,EPIC,Чертежи моста,NEW,Сделать проект моста через реку Волга,33120,%s\n",
                        epic2.getId(), subtask1.getStartTime().format(formatter)) +
                String.format("%d,SUBTASK,Пролетное строение,NEW,Начертить пролетное строение,%d,20160,%s\n",
                        subtask1.getId(), epic2.getId(), subtask1.getStartTime().format(formatter)) +
                String.format("%d,SUBTASK,Опоры,NEW,Начертить опоры,%d,11520,%s\n",
                        subtask2.getId(), epic2.getId(), subtask2.getStartTime().format(formatter));
        Assertions.assertEquals(expectedData, savedData);
    }

    @Test
    @Override
    public void taskManagerShouldDeleteSubtusk() throws IOException {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.deleteSubtask(subtask1.getId());
        String savedData = Files.readString(file.toPath());
        String expectedData = "Список сохраненных задач:\n" +
                String.format("%d,EPIC,Чертежи моста,NEW,Сделать проект моста через реку Волга,33120,%s\n",
                        epic2.getId(), subtask1.getStartTime().format(formatter)) +
                String.format("%d,SUBTASK,Опоры,NEW,Начертить опоры,%d,11520,%s\n",
                        subtask2.getId(), epic2.getId(), subtask2.getStartTime().format(formatter));
        Assertions.assertEquals(expectedData, savedData);
    }

    @Test
    @Override
    public void taskManagerShouldClearSubtusks() throws IOException {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.clearSubtasks();
        String savedData = Files.readString(file.toPath());
        String expectedData = "Список сохраненных задач:\n" +
                String.format("%d,EPIC,Чертежи моста,NEW,Сделать проект моста через реку Волга,33120,%s\n",
                        epic2.getId(), subtask1.getStartTime().format(formatter));
        Assertions.assertEquals(expectedData, savedData);
    }

    @Test
    @Override
    public void taskManagerShouldUpdateSubtusk() throws IOException {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        subtask3 = new Subtask(subtask1.getId(),
                "Пролетное строение",
                "Начертить пролетное строение",
                Status.INPROGRESS,
                subtask1.getSubtasksEpicId(),
                subtask1.getDuration(),
                subtask1.getStartTime());
        taskManager.updateSubtask(subtask3);
        String savedData = Files.readString(file.toPath());
        String expectedData = "Список сохраненных задач:\n" +
                String.format("%d,EPIC,Чертежи моста,INPROGRESS,Сделать проект моста через реку Волга,33120,%s\n",
                        epic2.getId(), subtask1.getStartTime().format(formatter)) +
                String.format("%d,SUBTASK,Пролетное строение,INPROGRESS,Начертить пролетное строение,%d,20160,%s\n",
                        subtask3.getId(), epic2.getId(), subtask3.getStartTime().format(formatter)) +
                String.format("%d,SUBTASK,Опоры,NEW,Начертить опоры,%d,11520,%s\n",
                        subtask2.getId(), epic2.getId(), subtask2.getStartTime().format(formatter));
        Assertions.assertEquals(expectedData, savedData);
    }
}
