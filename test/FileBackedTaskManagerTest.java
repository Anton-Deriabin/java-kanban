import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import status.Status;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    TaskManager inMemoryTaskManager = Managers.getDefault();

    private FileBackedTaskManager fileBackedTaskManager;
    private File file;

    @BeforeEach
    public void setUp() throws IOException {
        file = File.createTempFile("test", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(file);
    }

    @Test
    public void shouldSaveTasksToFile() throws IOException {
        Task task = new Task("Переезд", "Собрать вещи");
        Epic epic = new Epic("Чертежи моста", "Сделать проект моста через реку Волга");
        Subtask subtask = new Subtask("Опоры", "Начертить опоры", 2);
        fileBackedTaskManager.addTask(task);
        fileBackedTaskManager.addEpic(epic);
        fileBackedTaskManager.addSubtask(subtask);
        fileBackedTaskManager.save();
        String savedData = Files.readString(file.toPath());
        String expectedData = "id,type,name,status,description,epic\n" +
                task.getId() + ",TASK,Переезд,NEW,Собрать вещи,\n" +
                epic.getId() + ",EPIC,Чертежи моста,NEW,Сделать проект моста через реку Волга,\n" +
                subtask.getId() + ",SUBTASK,Опоры,NEW,Начертить опоры," + epic.getId() + "\n";
        assertEquals(expectedData, savedData);
        inMemoryTaskManager.setNextId(1);
    }

    @Test
    public void shouldLoadTasksFromFile() throws IOException {
        String fileContent = "id,type,name,status,description,epic\n" +
                "1,TASK,Test Task,NEW,Description,\n" +
                "2,EPIC,Test Epic,NEW,Epic Description,\n" +
                "3,SUBTASK,Test Subtask,NEW,Subtask Description,2\n";
        Files.writeString(file.toPath(), fileContent);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        HashMap<Integer, Task> tasks = loadedManager.printTasks();
        HashMap<Integer, Epic> epics = loadedManager.printEpics();
        HashMap<Integer, Subtask> subtasks = loadedManager.printSubtasks();
        assertEquals(1, tasks.size());
        assertEquals(1, epics.size());
        assertEquals(1, subtasks.size());
        Task task = tasks.get(1);
        Epic epic = epics.get(2);
        Subtask subtask = subtasks.get(3);
        assertEquals("Test Task", task.getName());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals("Test Epic", epic.getName());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals("Test Subtask", subtask.getName());
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(2, subtask.getSubtasksEpicId());
        inMemoryTaskManager.setNextId(1);
    }
}
