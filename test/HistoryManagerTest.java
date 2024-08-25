import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import status.Status;

import java.util.List;

public class HistoryManagerTest {
    private final TaskManager inMemoryTaskManager = Managers.getDefault();

    @Test
    public void historyManagerShouldPutCurrentTasks() {
        Task task1 = new Task("Переезд", "Собрать вещи");
        Epic epic1 = new Epic("Чертежи моста", "Сделать проект моста через реку Волга");
        Subtask subtask1 = new Subtask("Пролетное строение", "Начертить пролетное строение",
                2);
        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.getSubtask(3);
        List<Task> listTest= inMemoryTaskManager.getHistory();
        Assertions.assertEquals(task1, listTest.get(0), "Задача в листе не равна вызванной ранее");
        Assertions.assertEquals(epic1, listTest.get(1), "Задача в листе не равна вызванной ранее");
        Assertions.assertEquals(subtask1, listTest.get(2), "Задача в листе не равна вызванной ранее");

    }
}
