import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import status.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class InMemoryTaskManagerTest {
    TaskManager inMemoryTaskManager = Managers.getDefault();

    @Test
    public void inMemoryTaskManagerShouldPutDifferentTypeOfTasks() {
        Task task1 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        Epic epic1 = new Epic("Чертежи моста", "Сделать проект моста через реку Волга");
        Subtask subtask1 = new Subtask("Пролетное строение",
                "Начертить пролетное строение",
                2,
                Duration.ofDays(14),
                LocalDateTime.of(2024, 10, 13, 8, 0));
        Task task2 = inMemoryTaskManager.addTask(task1);
        Optional task2Optional = inMemoryTaskManager.getTask(1);
        Task task3 = (Task) task2Optional.get();
        Epic epic2 = inMemoryTaskManager.addEpic(epic1);
        Optional epic2Optional = inMemoryTaskManager.getEpic(2);
        Epic epic3 = (Epic) epic2Optional.get();
        Subtask subtask2 = inMemoryTaskManager.addSubtask(subtask1);
        Optional subtask3Optional = inMemoryTaskManager.getSubtask(3);
        Subtask subtask3 = (Subtask) subtask3Optional.get();
        Assertions.assertEquals(task2, task3, "Задачи не равны");
        Assertions.assertEquals(epic2, epic3, "Задачи не равны");
        Assertions.assertEquals(subtask2, subtask3, "Задачи не равны");
        inMemoryTaskManager.deleteEpic(2);
        Optional epic4Optional = inMemoryTaskManager.getEpic(2);
        Optional subtask4Optional = inMemoryTaskManager.getSubtask(3);
        Assertions.assertTrue(epic4Optional.isEmpty(), "Эпик не удален");
        Assertions.assertTrue(subtask4Optional.isEmpty(), "Подзадача не удалена вместе с эпиком");
        inMemoryTaskManager.setNextId(1);
    }

    @Test
    public void subtaskShouldUpdateEpicStatus() {
        Epic task1 = new Epic("Переезд", "Собрать вещи");
        inMemoryTaskManager.addEpic(task1);
        Subtask task2 = new Subtask("Пролетное строение",
                "Начертить пролетное строение",
                1,
                Duration.ofDays(14),
                LocalDateTime.of(2024, 10, 13, 8, 0));
        inMemoryTaskManager.addSubtask(task2);
        Subtask task3 = new Subtask("Опоры",
                "Начертить опоры",
                1,
                Duration.ofDays(8),
                LocalDateTime.of(2024, 10, 28, 8, 0));
        inMemoryTaskManager.addSubtask(task3);
        Epic task4 = new Epic("Переезд", "Собрать вещи", task1.getId(), task1.getStatus(), task1.getDuration(), task1.getStartTime());
        Assertions.assertEquals(task1, task4, "Эпики не равны");
        Subtask task5 = new Subtask(task2.getId(),
                "Пролетное строение",
                "Начертить пролетное строение",
                Status.INPROGRESS,
                task2.getSubtasksEpicId(),
                task2.getDuration(),
                task2.getStartTime());
        inMemoryTaskManager.updateSubtask(task5);
        task4.setStatus(Status.INPROGRESS);
        Assertions.assertEquals(task1, task4, "Эпики не равны");
        Subtask task6 = new Subtask(task2.getId(),
                "Пролетное строение",
                "Начертить пролетное строение",
                Status.DONE,
                task2.getSubtasksEpicId(),
                task2.getDuration(),
                task2.getStartTime());
        inMemoryTaskManager.updateSubtask(task6);
        Assertions.assertEquals(task1, task4, "Эпики не равны");
        Subtask task7 = new Subtask(task3.getId(),
                "Пролетное строение",
                "Начертить пролетное строение",
                Status.DONE,
                task3.getSubtasksEpicId(),
                task3.getDuration(),
                task3.getStartTime());
        inMemoryTaskManager.updateSubtask(task7);
        task4.setStatus(Status.DONE);
        Assertions.assertEquals(task1, task4, "Эпики не равны");
        inMemoryTaskManager.setNextId(1);
    }

    @Test
    public void addTaskShouldThrowException() {
        Epic epic1 = new Epic("Чертежи моста", "Сделать проект моста через реку Волга");
        inMemoryTaskManager.addEpic(epic1);
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
        inMemoryTaskManager.addTask(task1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            inMemoryTaskManager.addTask(task2);
        }, "Исключение не выброшено");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            inMemoryTaskManager.addTask(task3);
        }, "Исключение не выброшено");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            inMemoryTaskManager.addTask(task4);
        }, "Исключение не выброшено");
        inMemoryTaskManager.addSubtask(subtask1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            inMemoryTaskManager.addSubtask(subtask2);
        }, "Исключение не выброшено");
        inMemoryTaskManager.setNextId(1);
    }

    @Test
    public void addTaskShouldntThrowException() {
        Epic epic1 = new Epic("Чертежи моста", "Сделать проект моста через реку Волга");
        inMemoryTaskManager.addEpic(epic1);
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
        inMemoryTaskManager.addTask(task1);
        Assertions.assertDoesNotThrow(() -> {
            inMemoryTaskManager.addTask(task2);
        }, "Исключение не выброшено");
        inMemoryTaskManager.addSubtask(subtask1);
        Assertions.assertDoesNotThrow(() -> {
            inMemoryTaskManager.addSubtask(subtask2);
        }, "Исключение не выброшено");
        inMemoryTaskManager.setNextId(1);
    }
}
