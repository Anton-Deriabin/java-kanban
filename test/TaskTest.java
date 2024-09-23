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

public class TaskTest {
    TaskManager inMemoryTaskManager = Managers.getDefault();

    @Test
    public void taskShouldBeEqualsToTaskWithSameId() {
        Task task1 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        inMemoryTaskManager.addTask(task1);
        Task task2 = new Task("Переезд",
                "Собрать вещи",
                task1.getId(),
                task1.getStatus(),
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        Assertions.assertEquals(task1, task2, "Задачи не равны");
        task1.setStatus(Status.INPROGRESS);
        Assertions.assertNotEquals(task1.getStatus(), task2.getStatus(), "Статусы равны");
        Assertions.assertNotEquals(task1, task2, "Задачи равны");
        inMemoryTaskManager.setNextId(1);
    }

    @Test
    public void epicShouldBeEqualsToEpicWithSameId() {
        Epic task1 = new Epic("Переезд", "Собрать вещи");
        inMemoryTaskManager.addEpic(task1);
        Epic task2 = new Epic("Переезд", "Собрать вещи", task1.getStatus(), task1.getId());
        Assertions.assertEquals(task1, task2, "Эпики не равны");
        task2.setStatus(Status.INPROGRESS);
        Assertions.assertNotEquals(task1.getStatus(), task2.getStatus(), "Статусы равны");
        Assertions.assertNotEquals(task1, task2, "Эпики равны");
        inMemoryTaskManager.setNextId(1);
    }

    @Test
    public void subtaskShouldBeEqualsToSubtaskWithSameId() {
        Epic task1 = new Epic("Переезд", "Собрать вещи");
        inMemoryTaskManager.addEpic(task1);
        Subtask task2 = new Subtask("Пролетное строение",
                "Начертить пролетное строение",
                1,
                Duration.ofDays(14),
                LocalDateTime.of(2024, 10, 13, 8, 0));
        inMemoryTaskManager.addSubtask(task2);
        Subtask task3 = new Subtask(task2.getId(),
                "Пролетное строение",
                "Начертить пролетное строение",
                task2.getStatus(),
                task2.getSubtasksEpicId(),
                Duration.ofDays(14),
                LocalDateTime.of(2024, 10, 13, 8, 0));
        Assertions.assertEquals(task2, task3, "Задачи не равны");
        task3.setStatus(Status.INPROGRESS);
        Assertions.assertNotEquals(task2.getStatus(), task3.getStatus(), "Статусы равны");
        Assertions.assertNotEquals(task2, task3, "Подзадачи равны");
        Epic task4 = new Epic("Переезд", "Собрать вещи", task1.getId(), task1.getStatus(), task1.getDuration(), task1.getStartTime());
        Assertions.assertEquals(task1, task4, "Эпики не равны");
        inMemoryTaskManager.updateSubtask(task3);
        task4.setStatus(Status.INPROGRESS);
        Assertions.assertEquals(task1, task4, "Эпики не равны");
        inMemoryTaskManager.setNextId(1);
    }
}

