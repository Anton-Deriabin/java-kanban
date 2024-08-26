import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.*;

public class InMemoryTaskManagerTest {
    TaskManager inMemoryTaskManager = Managers.getDefault();

    @Test
    public void inMemoryTaskManagerShouldPutDifferentTypeOfTasks() {
        Task task1 = new Task("Переезд", "Собрать вещи");
        Epic epic1 = new Epic("Чертежи моста", "Сделать проект моста через реку Волга");
        Subtask subtask1 = new Subtask("Пролетное строение", "Начертить пролетное строение",
                2);
        Task task2 = inMemoryTaskManager.addTask(task1);
        Task task3 = inMemoryTaskManager.getTask(1);
        Epic epic2 = inMemoryTaskManager.addEpic(epic1);
        Epic epic3 = inMemoryTaskManager.getEpic(2);
        Subtask subtask2 = inMemoryTaskManager.addSubtask(subtask1);
        Subtask subtask3 = inMemoryTaskManager.getSubtask(3);
        Assertions.assertEquals(task2, task3, "Задачи не равны");
        Assertions.assertEquals(epic2 , epic3, "Задачи не равны");
        Assertions.assertEquals(subtask2 , subtask3, "Задачи не равны");
        inMemoryTaskManager.deleteEpic(2);
        Assertions.assertNull(inMemoryTaskManager.getEpic(2), "Эпик не удален");
        Assertions.assertNull(inMemoryTaskManager.getSubtask(3), "Подзадача не удалена вместе с эпиком");
        inMemoryTaskManager.setNextId(1);
    }
}

