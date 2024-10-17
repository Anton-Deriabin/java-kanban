import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task1;
    protected Task task2;
    protected Epic epic1;
    protected Epic epic2;
    protected Subtask subtask1;
    protected Subtask subtask2;


    @BeforeEach
    public void setUp() throws IOException {
        taskManager = (T) Managers.getDefault();
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
    public void setNextId() {
        taskManager.setNextId(1);
    }

    @Test
    public void taskManagerShouldPrintTasks() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Assertions.assertEquals(task1, taskManager.printTasks().get(task1.getId()),
                "Задачи нет в списке задач");
        Assertions.assertEquals(task2, taskManager.printTasks().get(task2.getId()),
                "Задачи нет в списке задач");
    }

    @Test
    public void taskManagerShouldGetTask() {
        Task task2 = taskManager.addTask(task1);
        Optional task2Optional = taskManager.getTask(1);
        Task task3 = (Task) task2Optional.get();
        Assertions.assertEquals(task2, task3, "Задачи не равны");
    }

    @Test
    public void taskManagerShouldPrintEpics() {
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        Assertions.assertEquals(epic1, taskManager.printEpics().get(epic1.getId()),
                "Эпика нет в списке эпиков");
        Assertions.assertEquals(epic2, taskManager.printEpics().get(epic2.getId()),
                "Эпика нет в списке эпиков");
    }

    @Test
    public void taskManagerShouldGetEpic() {
        Epic epic2 = taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        Optional epic2Optional = taskManager.getEpic(1);
        Epic epic3 = (Epic) epic2Optional.get();
        Assertions.assertEquals(epic2, epic3, "Эпики не равны");
    }

    @Test
    public void taskManagerShouldPrintSubtasks() {
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        Assertions.assertEquals(subtask1, taskManager.printSubtasks().get(subtask1.getId()),
                "Подзадачи нет в списке подзадач");
        Assertions.assertEquals(subtask2, taskManager.printSubtasks().get(subtask2.getId()),
                "Подзадачи нет в списке подзадач");
    }

    @Test
    public void taskManagerShouldGetSubtask() {
        taskManager.addEpic(epic1);
        Subtask subtask2 = taskManager.addSubtask(subtask1);
        Optional subtask3Optional = taskManager.getSubtask(2);
        Subtask subtask3 = (Subtask) subtask3Optional.get();
        Assertions.assertEquals(subtask2, subtask3, "Подзадачи не равны");
    }

    @Test
    public void taskManagerShouldPrintSubtusksOfEpic() {
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        Assertions.assertEquals(subtask1, taskManager.printSubtusksOfEpic(epic1).get(subtask1.getId()),
                "Подзадачи нет в списке у эпика");
        Assertions.assertEquals(subtask2, taskManager.printSubtusksOfEpic(epic1).get(subtask2.getId()),
                "Подзадачи нет в списке у эпика");
    }

    @Test
    public void addTaskShouldThrowException() {
        taskManager.addEpic(epic2);
        Task task1 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        Task task2 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 11, 19));
        Task task3 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 9, 23, 10, 21));
        Task task4 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 9, 21));
        Subtask subtask1 = new Subtask("Пролетное строение",
                "Начертить пролетное строение",
                1,
                Duration.ofDays(14),
                LocalDateTime.of(2024, 10, 13, 8, 0));
        Subtask subtask2 = new Subtask("Опоры",
                "Начертить опоры",
                1,
                Duration.ofDays(8),
                LocalDateTime.of(2024, 10, 15, 8, 0));
        taskManager.addTask(task1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addTask(task2);
        }, "Исключение не выброшено");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addTask(task3);
        }, "Исключение не выброшено");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addTask(task4);
        }, "Исключение не выброшено");
        taskManager.addSubtask(subtask1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addSubtask(subtask2);
        }, "Исключение не выброшено");

    }

    @Test
    public void addTaskShouldntThrowException() {
        taskManager.addEpic(epic2);
        Task task1 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        Task task2 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 8, 19));
        Subtask subtask1 = new Subtask("Пролетное строение",
                "Начертить пролетное строение",
                1,
                Duration.ofDays(1),
                LocalDateTime.of(2024, 10, 13, 8, 0));
        Subtask subtask2 = new Subtask("Опоры",
                "Начертить опоры",
                1,
                Duration.ofDays(2),
                LocalDateTime.of(2024, 10, 16, 8, 0));
        taskManager.addTask(task1);
        Assertions.assertDoesNotThrow(() -> {
            taskManager.addTask(task2);
        }, "Исключение выброшено");
        taskManager.addSubtask(subtask1);
        Assertions.assertDoesNotThrow(() -> {
            taskManager.addSubtask(subtask2);
        }, "Исключение выброшено");

    }

    @Test
    public abstract void taskManagerShouldAddTask() throws IOException;

    @Test
    public abstract void taskManagerShouldDeleteTask() throws IOException;

    @Test
    public abstract void taskManagerShouldClearTasks() throws IOException;

    @Test
    public abstract void taskManagerShouldUpdateTask() throws IOException;

    @Test
    public abstract void taskManagerShouldAddEpic() throws IOException;

    @Test
    public abstract void taskManagerShouldDeleteEpic() throws IOException;

    @Test
    public abstract void taskManagerShouldClearEpics() throws IOException;

    @Test
    public abstract void taskManagerShouldUpdateEpic() throws IOException;

    @Test
    public abstract void taskManagerShouldAddSubtusk() throws IOException;

    @Test
    public abstract void taskManagerShouldDeleteSubtusk() throws IOException;

    @Test
    public abstract void taskManagerShouldClearSubtusks() throws IOException;

    @Test
    public abstract void taskManagerShouldUpdateSubtusk() throws IOException;
}


