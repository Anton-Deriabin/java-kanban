import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.*;
import status.Status;

public class TaskTest {
    TaskManager inMemoryTaskManager = Managers.getDefault();

    @Test
    public void taskShouldBeEqualsToTaskWithSameId() {
        Task task1 = new Task("Переезд", "Собрать вещи");
        inMemoryTaskManager.addTask(task1);
        Task task2 = new Task("Переезд", "Собрать вещи", Status.NEW, task1.getId());
        Assertions.assertEquals(task1.getName(), task2.getName(), "Имена не равны");
        Assertions.assertEquals(task1.getDescription(), task2.getDescription(), "Описания не равны");
        Assertions.assertEquals(task1.getStatus(), task2.getStatus(), "Статусы не равны");
        task1.setStatus(Status.INPROGRESS);
        Assertions.assertNotEquals(task1.getStatus(), task2.getStatus(), "Статусы равны");
        Assertions.assertEquals(task1, task2, "Задачи не равны");
    }

    @Test
    public void epicShouldBeEqualsToEpicWithSameId() {
        Epic task1 = new Epic("Переезд", "Собрать вещи");
        inMemoryTaskManager.addEpic(task1);
        Epic task2 = new Epic("Переезд", "Собрать вещи", task1.getId());
        Assertions.assertEquals(task1.getName(), task2.getName(), "Имена не равны");
        Assertions.assertEquals(task1.getDescription(), task2.getDescription(), "Описания не равны");
        Assertions.assertEquals(task1.getStatus(), task2.getStatus(), "Статусы не равны");
        task2.setStatus(Status.INPROGRESS);
        Assertions.assertNotEquals(task1.getStatus(), task2.getStatus(), "Статусы равны");
        Assertions.assertEquals(task1, task2, "Эпики не равны");
    }

    @Test
    public void subtaskShouldBeEqualsToSubtaskWithSameId() {
        Epic task1 = new Epic("Переезд", "Собрать вещи");
        inMemoryTaskManager.addEpic(task1);
        Subtask task2 = new Subtask("Собрать документы", "Взять паспорт", task1.getId());
        inMemoryTaskManager.addSubtask(task2);
        Subtask task3 = new Subtask("Собрать документы", "Взять паспорт", Status.NEW, task2.getId(),
                task1.getId());
        Assertions.assertEquals(task2.getName(), task3.getName(), "Имена не равны");
        Assertions.assertEquals(task2.getDescription(), task3.getDescription(), "Описания не равны");
        Assertions.assertEquals(task2.getStatus(), task3.getStatus(), "Статусы не равны");
        task3.setStatus(Status.INPROGRESS);
        Assertions.assertNotEquals(task2.getStatus(), task3.getStatus(), "Статусы равны");
        Assertions.assertEquals(task2, task3, "Задачи не равны");
    }
}
